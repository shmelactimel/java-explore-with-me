package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.FeedbackDto;
import ru.practicum.comment.dto.FeedbackStatusUpdateRequest;
import ru.practicum.comment.dto.NewFeedbackDto;
import ru.practicum.comment.mapper.FeedbackMapper;
import ru.practicum.comment.model.Feedback;
import ru.practicum.comment.model.FeedbackStatus;
import ru.practicum.comment.repository.FeedbackRepository;
import ru.practicum.comment.repository.FeedbackSpecRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.IncorrectRequestException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.exception.RequestConflictException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;
import static ru.practicum.comment.repository.FeedbackSpecRepository.*;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackSpecRepository feedbackSpecRepository;
    private final FeedbackMapper feedbackMapper;

    @Override
    public FeedbackDto addUserFeedback(Long userId, Long eventId, NewFeedbackDto newFeedbackDto) {
        User feedbacker = userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("User with id = " + userId + " was not found.");
        });

        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Event with id = " + eventId + " doesn't exist.");
        });

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new RequestConflictException("Users are not allowed to feedback on unpublished events.");
        }

        Feedback feedback = feedbackMapper.newFeedbackDtoToFeedback(newFeedbackDto);
        feedback.setUser(feedbacker);
        feedback.setEvent(event);
        feedback.setCreatedOn(LocalDateTime.now());
        feedback.setStatus(FeedbackStatus.PENDING);

        feedback = feedbackRepository.save(feedback);

        return feedbackMapper.feedbackToFeedbackDto(feedback);
    }

    @Override
    public FeedbackDto updateUserFeedback(Long userId, Long eventId, Long feedbackId, NewFeedbackDto newFeedbackDto) {
        Feedback feedback = feedbackRepository.findByIdAndUserIdAndEventId(feedbackId, userId, eventId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Comment with id = " + feedbackId + " by user id = " + userId +
                    " for event id = " + eventId + " doesn't exist.");
        });

        if (feedback.getStatus().equals(FeedbackStatus.PENDING)) {
            throw new RequestConflictException("Users are not allowed to update feedbacks, which are pending moderation.");
        }

        feedback.setText(newFeedbackDto.getText());
        feedback.setCreatedOn(LocalDateTime.now());
        feedback.setStatus(FeedbackStatus.PENDING);

        feedback = feedbackRepository.save(feedback);

        return feedbackMapper.feedbackToFeedbackDto(feedback);
    }

    @Override
    public FeedbackDto getUserEventFeedback(Long userId, Long eventId, Long feedbackId) {
        Feedback feedback = feedbackRepository.findByIdAndUserIdAndEventId(feedbackId, userId, eventId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Feedback with id = " + feedbackId + " by user id = " + userId +
                    " for event id = " + eventId + " doesn't exist.");
        });

        if (feedback.getStatus().equals(FeedbackStatus.PENDING)) {
            throw new RequestConflictException("Users are not allowed to review feedbacks, which are pending moderation.");
        }

        return feedbackMapper.feedbackToFeedbackDto(feedback);
    }

    @Override
    public List<FeedbackDto> getAllUserFeedbacks(Long userId) {
        List<Feedback> feedbacks = feedbackRepository.findAllByUserIdAndStatus(userId, FeedbackStatus.PUBLISHED);

        if (!feedbacks.isEmpty()) {
            return feedbacks.stream()
                    .map(feedbackMapper::feedbackToFeedbackDto)
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    @Override
    public List<FeedbackDto> getAllUserEventFeedbacks(Long userId, Long eventId) {
        List<Feedback> feedbacks = feedbackRepository.findAllByUserIdAndEventIdAndStatus(userId, eventId, FeedbackStatus.PUBLISHED);

        if (!feedbacks.isEmpty()) {
            return feedbacks.stream()
                    .map(feedbackMapper::feedbackToFeedbackDto)
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    @Override
    @Transactional
    public void deleteUserFeedback(Long userId, Long eventId, Long feedbackId) {
        if (feedbackRepository.existsByIdAndUserIdAndEventIdAndStatus(feedbackId, userId, eventId, FeedbackStatus.PUBLISHED)) {
            feedbackRepository.deleteByIdAndUserIdAndEventIdAndStatus(feedbackId, userId, eventId, FeedbackStatus.PUBLISHED);
        } else {
            throw new ObjectNotFoundException("Feedback with id = " + feedbackId + " by user id = " + userId +
                    " for event id = " + eventId + " is pending moderation or doesn't exist.");
        }
    }

    @Override
    public List<FeedbackDto> getAdminFeedbacks(String text, List<Long> users, List<FeedbackStatus> statuses, List<Long> events,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                             Integer from, Integer size) {

        if ((rangeStart != null && rangeEnd != null) && (rangeStart.isAfter(rangeEnd) || rangeStart.isEqual(rangeEnd))) {
            throw new IncorrectRequestException("Start time must not after or equal to end time.");
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());

        Page<Feedback> feedbacksPage = feedbackSpecRepository.findAll(where(hasText(text))
                .and(hasUsers(users))
                .and(hasStatuses(statuses))
                .and(hasEvents(events))
                .and(hasRangeStart(rangeStart))
                .and(hasRangeEnd(rangeEnd)), pageable);

        return feedbacksPage.stream()
                .map(feedbackMapper::feedbackToFeedbackDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackDto> moderateAdminFeedbacks(FeedbackStatusUpdateRequest updateRequest) {
        List<Feedback> feedbacks = feedbackRepository.findAllByIdInAndStatus(updateRequest.getFeedbackIds(), FeedbackStatus.PENDING);

        if (feedbacks.size() != updateRequest.getFeedbackIds().size()) {
            throw new ObjectNotFoundException("Incorrect feedback id(s) in the request body.");
        }

        switch (updateRequest.getStatus()) {
            case PUBLISHED:
                feedbacks.forEach(feedback -> feedback.setStatus(FeedbackStatus.PUBLISHED));
                feedbacks = feedbackRepository.saveAll(feedbacks);
                return feedbacks.stream()
                        .map(feedbackMapper::feedbackToFeedbackDto)
                        .collect(Collectors.toList());
            case DELETED:
                feedbacks.forEach(feedback -> feedbackRepository.deleteAllById(updateRequest.getFeedbackIds()));
                return new ArrayList<>();
            default:
                throw new IncorrectRequestException("Incorrect admin moderate request with status 'Pending'.");
        }
    }
}