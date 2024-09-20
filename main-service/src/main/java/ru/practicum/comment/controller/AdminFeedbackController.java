package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.FeedbackDto;
import ru.practicum.comment.dto.FeedbackStatusUpdateRequest;
import ru.practicum.comment.model.FeedbackStatus;
import ru.practicum.comment.service.FeedbackService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/feedbacks")
@RequiredArgsConstructor
@Slf4j
public class AdminFeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping
    public List<FeedbackDto> getAdminFeedbacks(@RequestParam(name = "text", required = false) String text,
                                             @RequestParam(name = "users", required = false) List<Long> users,
                                             @RequestParam(name = "statuses", required = false) List<FeedbackStatus> statuses,
                                             @RequestParam(name = "events", required = false) List<Long> events,
                                             @RequestParam(name = "rangeStart", required = false)
                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                             @RequestParam(name = "rangeEnd", required = false)
                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                             @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("Calling GET: /admin/feedbacks with 'test': {}, 'users': {}, 'statuses': {}, 'events': {}, 'rangeStart': {}, " +
                "'rangeEnd': {}, 'from': {}, 'size': {}", text, users, statuses, events, rangeStart, rangeEnd, from, size);
        return feedbackService.getAdminFeedbacks(text, users, statuses, events, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping
    public List<FeedbackDto> moderateAdminFeedbacks(@RequestBody FeedbackStatusUpdateRequest updateRequest) {

        log.info("Calling PATCH: /admin/feedbacks with 'updateRequest': {}", updateRequest);
        return feedbackService.moderateAdminFeedbacks(updateRequest);
    }
}