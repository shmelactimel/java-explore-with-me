package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.comment.model.FeedbackStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackStatusUpdateRequest {

    @NotNull
    @NotEmpty
    private List<Long> feedbackIds;

    @NotNull
    @Enumerated(EnumType.STRING)
    private FeedbackStatus status;
}