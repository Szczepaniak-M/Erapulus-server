package com.erapulus.server.module.mapper;

import com.erapulus.server.TestUtils;
import com.erapulus.server.module.database.ModuleEntity;
import com.erapulus.server.module.dto.ModuleResponseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModuleEntityToResponseDtoMapperTest {

    private static final int ID = 1;
    private static final String NAME = "name";
    private static final String ABBREV = "abbrev";
    private static final String DESCRIPTION = "description";
    private static final int PROGRAM_ID = 2;

    @Test
    void from_shouldMapEntityToDto() {
        // given
        ModuleEntity entity = ModuleEntity.builder()
                                          .id(ID)
                                          .name(NAME)
                                          .abbrev(ABBREV)
                                          .description(DESCRIPTION)
                                          .programId(PROGRAM_ID)
                                          .build();

        // when
        ModuleResponseDto result = new ModuleEntityToResponseDtoMapper().from(entity);

        //then
        assertEquals(ID, result.id());
        assertEquals(NAME, result.name());
        assertEquals(ABBREV, result.abbrev());
        assertEquals(DESCRIPTION, result.description());
        assertEquals(PROGRAM_ID, result.programId());
        assertTrue(TestUtils.createValidator().validate(result).isEmpty());
    }
}