package com.erapulus.server.mapper.building;

import com.erapulus.server.database.model.BuildingEntity;
import com.erapulus.server.dto.building.BuildingRequestDto;
import com.erapulus.server.mapper.ValidatorUtils;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BuildingRequestDtoToEntityMapperTest {

    private static final String NAME = "name";
    private static final String ABBREV = "abbrev";
    private static final double LONGITUDE = 1.5;
    private static final double LATITUDE = 2.5;

    @Test
    void from_shouldMapDtoToEntity() {
        // given
        BuildingRequestDto requestDto = BuildingRequestDto.builder()
                                                          .name(NAME)
                                                          .abbrev(ABBREV)
                                                          .longitude(LONGITUDE)
                                                          .latitude(LATITUDE)
                                                          .build();

        // when
        BuildingEntity result = new BuildingRequestDtoToEntityMapper().from(requestDto);

        //then
        assertNull(result.id());
        assertEquals(NAME, result.name());
        assertEquals(ABBREV, result.abbrev());
        assertEquals(LONGITUDE, result.longitude());
        assertEquals(LATITUDE, result.latitude());
        assertNull(result.universityId());
        assertThat(ValidatorUtils.getValidationResult(result)).containsExactlyInAnyOrderElementsOf(List.of("id", "universityId"));
    }
}