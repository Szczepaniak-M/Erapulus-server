package com.erapulus.server.program.mapper;

import com.erapulus.server.TestUtils;
import com.erapulus.server.program.database.ProgramEntity;
import com.erapulus.server.program.dto.ProgramRequestDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProgramRequestDtoToEntityMapperTest {

    private static final String NAME = "name";
    private static final String ABBREV = "abbrev";
    private static final String DESCRIPTION = "description";

    @Test
    void from_shouldMapDtoToEntity() {
        // given
        ProgramRequestDto requestDto = ProgramRequestDto.builder()
                                                        .name(NAME)
                                                        .abbrev(ABBREV)
                                                        .description(DESCRIPTION)
                                                        .build();

        // when
        ProgramEntity result = new ProgramRequestDtoToEntityMapper().from(requestDto);

        //then
        assertNull(result.id());
        assertEquals(NAME, result.name());
        assertEquals(ABBREV, result.abbrev());
        assertEquals(DESCRIPTION, result.description());
        assertNull(result.facultyId());
        assertThat(TestUtils.getValidationResult(result)).containsExactlyInAnyOrderElementsOf(List.of("id", "facultyId"));
    }
}