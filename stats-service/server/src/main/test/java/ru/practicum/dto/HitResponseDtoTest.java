package ru.practicum.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.HitResponseDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class HitResponseDtoTest {

    @Autowired
    private JacksonTester<HitResponseDto> jacksonTester;

    @Test
    void testSerialize() throws Exception {
        HitResponseDto hitResponseDto = new HitResponseDto("ewm-main-service", "/events/1", 2L);

        JsonContent<HitResponseDto> hitResponseDtoSaved = jacksonTester.write(hitResponseDto);

        assertThat(hitResponseDtoSaved).hasJsonPath("$.app");
        assertThat(hitResponseDtoSaved).hasJsonPath("$.uri");
        assertThat(hitResponseDtoSaved).hasJsonPath("$.hits");

        assertThat(hitResponseDtoSaved).extractingJsonPathStringValue("$.app").isEqualTo(hitResponseDto.getApp());
        assertThat(hitResponseDtoSaved).extractingJsonPathStringValue("$.uri").isEqualTo(hitResponseDto.getUri());
        assertThat(hitResponseDtoSaved).extractingJsonPathNumberValue("$.hits").isEqualTo(hitResponseDto.getHits().intValue());
    }
}
