package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFeedbackDto {
    @NotBlank
    @Size(min = 5, max = 5000)
    private String text;

    @Positive
    private Long feedbackId;

    @Positive
    private Long eventId;
}
