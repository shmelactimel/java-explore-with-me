package ru.practicum.comment.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Feedback;
import ru.practicum.comment.model.FeedbackStatus;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackSpecRepository extends JpaRepository<Feedback, Long>, JpaSpecificationExecutor<Feedback> {

    static Specification<Feedback> hasText(String text) {
        return (event, query, critBuilder) -> {
            if (text == null) {
                return critBuilder.isTrue(critBuilder.literal(true));
            } else {
                return critBuilder.like(critBuilder.lower(event.get("text")), "%" + text.toLowerCase() + "%");
            }
        };
    }

    static Specification<Feedback> hasEvents(List<Long> events) {
        return (event, query, critBuilder) -> {
            if (events == null || events.size() == 0) {
                return critBuilder.isTrue(critBuilder.literal(true));
            } else {
                CriteriaBuilder.In<Long> eventIds = critBuilder.in(event.get("event"));
                for (long eventId : events) {
                    eventIds.value(eventId);
                }
                return eventIds;
            }
        };
    }

    static Specification<Feedback> hasUsers(List<Long> users) {
        return (event, query, critBuilder) -> {
            if (users == null || users.size() == 0) {
                return critBuilder.isTrue(critBuilder.literal(true));
            } else {
                CriteriaBuilder.In<Long> userIds = critBuilder.in(event.get("feedbacker"));
                for (long userId : users) {
                    userIds.value(userId);
                }
                return userIds;
            }
        };
    }

    static Specification<Feedback> hasStatuses(List<FeedbackStatus> statuses) {
        return (event, query, critBuilder) -> {
            if (statuses == null || statuses.size() == 0) {
                return critBuilder.isTrue(critBuilder.literal(true));
            } else {
                CriteriaBuilder.In<FeedbackStatus> feedbackStatuses = critBuilder.in(event.get("status"));
                for (FeedbackStatus status : statuses) {
                    feedbackStatuses.value(status);
                }
                return feedbackStatuses;
            }
        };
    }

    static Specification<Feedback> hasRangeStart(LocalDateTime rangeStart) {
        return (event, query, critBuilder) -> {
            if (rangeStart == null) {
                return critBuilder.isTrue(critBuilder.literal(true));
            } else {
                return critBuilder.greaterThan(event.get("createdOn"), rangeStart);
            }
        };
    }

    static Specification<Feedback> hasRangeEnd(LocalDateTime rangeEnd) {
        return (event, query, critBuilder) -> {
            if (rangeEnd == null) {
                return critBuilder.isTrue(critBuilder.literal(true));
            } else {
                return critBuilder.lessThan(event.get("createdOn"), rangeEnd);
            }
        };
    }
}