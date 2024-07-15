package ru.practicum.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.HitRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JsonTest
public class HitRequestDtoTest {

    @Autowired
    private JacksonTester<HitRequestDto> jacksonTester;

    private HitRequestDto hitRequestDto;

    @BeforeEach
    void setup() {
        hitRequestDto = new HitRequestDto(1, "ewm-main-service", "/events/1", "192.163.0.1", LocalDateTime.now());
    }

    @Test
    void serialize() throws Exception {
        JsonContent<HitRequestDto> hitRequestDtoSaved = jacksonTester.write(hitRequestDto);

        assertThat(hitRequestDtoSaved).hasJsonPath("$.id");
        assertThat(hitRequestDtoSaved).hasJsonPath("$.app");
        assertThat(hitRequestDtoSaved).hasJsonPath("$.uri");
        assertThat(hitRequestDtoSaved).hasJsonPath("$.ip");
        assertThat(hitRequestDtoSaved).hasJsonPath("$.timestamp");

        assertThat(hitRequestDtoSaved).extractingJsonPathNumberValue("$.id").isEqualTo(hitRequestDto.getId());
        assertThat(hitRequestDtoSaved).extractingJsonPathStringValue("$.app").isEqualTo(hitRequestDto.getApp());
        assertThat(hitRequestDtoSaved).extractingJsonPathStringValue("$.uri").isEqualTo(hitRequestDto.getUri());
        assertThat(hitRequestDtoSaved).extractingJsonPathStringValue("$.ip").isEqualTo(hitRequestDto.getIp());

        assertThat(hitRequestDtoSaved).hasJsonPathValue("$.timestamp");
    }

    @Test
    void deserialize() throws Exception {
        String json = "{\"app\":\"main-service\"," +
                "\"uri\":\"/events/1\"," +
                "\"ip\":\"192.163.0.1\"}";

        HitRequestDto hitRequestDto = jacksonTester.parseObject(json);

        assertNotNull(hitRequestDto);

        assertThat(hitRequestDto.getApp()).isEqualTo(hitRequestDto.getApp());
        assertThat(hitRequestDto.getUri()).isEqualTo(hitRequestDto.getUri());
        assertThat(hitRequestDto.getIp()).isEqualTo(hitRequestDto.getIp());
    }
}
