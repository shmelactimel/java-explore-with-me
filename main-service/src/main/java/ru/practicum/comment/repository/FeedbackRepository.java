package ru.practicum.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Feedback;
import ru.practicum.comment.model.FeedbackStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Optional<Feedback> findByIdAndUserIdAndEventId(Long feedbackId, Long userId, Long eventId);

    List<Feedback> findAllByUserIdAndStatus(Long userId, FeedbackStatus published);

    List<Feedback> findAllByUserIdAndEventIdAndStatus(Long userId, Long eventId, FeedbackStatus published);

    void deleteByIdAndUserIdAndEventIdAndStatus(Long feedbackId, Long userId, Long eventId, CommentStatus published);

    List<Feedback> findAllByIdInAndStatus(List<Long> feedbackIds, FeedbackStatus status);

    boolean existsByIdAndUserIdAndEventIdAndStatus(Long feedbackId, Long userId, Long eventId, FeedbackStatus published);
}