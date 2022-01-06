package com.erapulus.server.faculty.mapper;

import com.erapulus.server.TestUtils;
import com.erapulus.server.faculty.database.FacultyEntity;
import com.erapulus.server.faculty.dto.FacultyRequestDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FacultyRequestDtoToEntityMapperTest {

    private static final String NAME = "name";
    private static final String EMAIL = "example@gmail.com";
    private static final String ADDRESS = "address";

    @Test
    void from_shouldMapDtoToEntity() {
        // given
        FacultyRequestDto requestDto = FacultyRequestDto.builder()
                                                        .name(NAME)
                                                        .address(ADDRESS)
                                                        .email(EMAIL)
                                                        .build();

        // when
        FacultyEntity result = new FacultyRequestDtoToEntityMapper().from(requestDto);

        //then
        assertNull(result.id());
        assertEquals(NAME, result.name());
        assertEquals(EMAIL, result.email());
        assertEquals(ADDRESS, result.address());
        assertNull(result.universityId());
        assertThat(TestUtils.getValidationResult(result)).containsExactlyInAnyOrderElementsOf(List.of("id", "universityId"));
    }
}