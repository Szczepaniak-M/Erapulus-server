package com.erapulus.server.mapper.program;

import com.erapulus.server.database.model.ProgramEntity;
import com.erapulus.server.dto.program.ProgramRequestDto;
import com.erapulus.server.mapper.ValidatorUtils;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
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
        assertThat(ValidatorUtils.getValidationResult(result)).containsExactlyInAnyOrderElementsOf(List.of("id", "facultyId"));
    }
}