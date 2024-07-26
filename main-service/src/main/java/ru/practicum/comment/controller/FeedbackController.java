package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.FeedbackDto;
import ru.practicum.comment.dto.NewFeedbackDto;
import ru.practicum.comment.service.FeedbackService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@Slf4j
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/events/{eventId}/feedbacks")
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackDto createUserFeedback(@PathVariable @Positive Long userId,
                                        @PathVariable @Positive Long eventId,
                                        @RequestBody @Valid NewFeedbackDto newFeedbackDto) {

        log.info("Calling POST: /users/{userId}/events/{eventId}/Feedbacks with 'userId': {}, 'eventId': {}," +
                " 'newFeedbackDto': {}", userId, eventId, newFeedbackDto);
        return feedbackService.addUserFeedback(userId, eventId, newFeedbackDto);
    }

    @GetMapping ("/events/{eventId}/feedbacks/{feedbackId}")
    public FeedbackDto getUserFeedback(@PathVariable @Positive Long userId,
                                     @PathVariable @Positive Long eventId,
                                     @PathVariable @Positive Long feedbacktId) {

        log.info("Calling GET: /users/{userId}/events/{eventId}/feedbacks/{feedbackId} with 'userId': {}, 'eventId': {}," +
                " 'feedbackId': {}", userId, eventId, feedbackId);
        return feedbackService.getUserEventFeedback(userId, eventId, feedbackId);
    }

    @GetMapping ("/events/{eventId}/feedbacks")
    public List<FeedbackDto> getUserEventFeedbacks(@PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long eventId) {

        log.info("Calling GET: /users/{userId}/events/{eventId}/feedbacks with 'userId': {}, 'eventId': {},", userId, eventId);
        return feedbackService.getAllUserEventFeedbacks(userId, eventId);
    }

    @GetMapping ("/feedbacks")
    public List<FeedbackDto> getUserFeedbacks(@PathVariable @Positive Long userId) {

        log.info("Calling POST: /users/{userId}/events/{eventId}/feedbacks with 'userId': {}", userId);
        return feedbackService.getAllUserFeedbacks(userId);
    }

    @PatchMapping("/events/{eventId}/feedbacks/{feedbackId}")
    public FeedbackDto updateUserFeedback(@PathVariable @Positive Long userId,
                                        @PathVariable @Positive Long eventId,
                                        @PathVariable @Positive Long feedbackId,
                                        @RequestBody @Valid NewFeedbackDto newFeedbackDto) {

        log.info("Calling PATCH: /users/{userId}/events/{eventId}/feedbacks/{feedbackId} with 'userId': {}, 'eventId': {}," +
                " , 'feedbackId': {}, 'newFeedbackDto': {}", userId, eventId, feedbackId, newFeedbackDto);
        return feedbackService.updateUserFeedback(userId, eventId, feedbackId, newFeedbackDto);
    }

    @DeleteMapping("/events/{eventId}/feedbacks/{feedbackId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserFeedback(@PathVariable @Positive Long userId,
                                  @PathVariable @Positive Long eventId,
                                  @PathVariable @Positive Long feedbackId) {

        log.info("Calling DELETE: /users/{userId}/events/{eventId}/feedbacks/{feedbackId} with 'userId': {}, 'eventId': {}," +
                " , 'feedbackId': {}", userId, eventId, feedbackId);
        feedbackService.deleteUserFeedback(userId, eventId, feedbackId);
    }
}