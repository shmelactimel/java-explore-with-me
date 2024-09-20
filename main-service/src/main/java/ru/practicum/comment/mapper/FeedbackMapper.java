package ru.practicum.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.comment.dto.FeedbackDto;
import ru.practicum.comment.dto.NewFeedbackDto;
import ru.practicum.comment.model.Feedback;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {
    FeedbackMapper INSTANCE = Mappers.getMapper(FeedbackMapper.class);

    Feedback newFeedbackDtoToFeedback(NewFeedbackDto newFeedbackDto);

    FeedbackDto feedbackToFeedbackDto(Feedback feedback);
}