package com.erapulus.server.program.mapper;

import com.erapulus.server.TestUtils;
import com.erapulus.server.program.database.ProgramEntity;
import com.erapulus.server.program.dto.ProgramResponseDto;
import org.junit.jupiter.api.Test;

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
        assertTrue(TestUtils.createValidator().validate(result).isEmpty());
    }
}