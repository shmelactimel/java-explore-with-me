package ru.practicum.category.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto extends NewCategoryDto {
    private Long id;
}
