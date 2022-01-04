package com.erapulus.server.mapper.program;

import com.erapulus.server.database.model.ProgramEntity;
import com.erapulus.server.dto.program.ProgramResponseDto;
import com.erapulus.server.mapper.ValidatorUtils;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgramEntityToResponseDtoMapperTest {

    private static final int ID = 1;
    private static final String NAME = "name";
    private static final String ABBREV = "abbrev";
    private static final String DESCRIPTION = "description";
    private static final int FACULTY_ID = 2;

    @Test
    void from_shouldMapEntityToDto() {
        // given
        ProgramEntity entity = ProgramEntity.builder()
                                          .id(ID)
                                          .name(NAME)
                                          .abbrev(ABBREV)
                                          .description(DESCRIPTION)
                                          .facultyId(FACULTY_ID)
                                          .build();

        // when
        ProgramResponseDto result = new ProgramEntityToResponseDtoMapper().from(entity);

        //then
        assertEquals(ID, result.id());
        assertEquals(NAME, result.name());
        assertEquals(ABBREV, result.abbrev());
        assertEquals(DESCRIPTION, result.description());
        assertEquals(FACULTY_ID, result.facultyId());
        assertTrue(ValidatorUtils.createValidator().validate(result).isEmpty());
    }
}