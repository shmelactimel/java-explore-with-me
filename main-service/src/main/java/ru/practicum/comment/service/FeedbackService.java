package ru.practicum.comment.service;

import ru.practicum.comment.dto.FeedbackDto;
import ru.practicum.comment.dto.FeedbackStatusUpdateRequest;
import ru.practicum.comment.dto.NewFeedbackDto;
import ru.practicum.comment.model.FeedbackStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackService {

    FeedbackDto addUserFeedback(Long userId, Long eventId, NewFeedbackDto newFeedbackDto);

    FeedbackDto updateUserFeedback(Long userId, Long eventId, Long feedbackId, NewFeedbackDto newFeedbackDto);

    void deleteUserFeedback(Long userId, Long eventId, Long feedbackId);

    FeedbackDto getUserEventFeedback(Long userId, Long eventId, Long feedbackId);

    List<FeedbackDto> getAllUserFeedbacks(Long userId);

    List<FeedbackDto> getAllUserEventFeedbacks(Long userId, Long eventId);

    List<FeedbackDto> getAdminFeedbacks(String text, List<Long> users, List<FeedbackStatus> statuses, List<Long> events, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    List<FeedbackDto> moderateAdminFeedbacks(FeedbackStatusUpdateRequest updateRequest);
}