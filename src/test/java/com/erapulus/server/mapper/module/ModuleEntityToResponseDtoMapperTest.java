package com.erapulus.server.mapper.module;

import com.erapulus.server.database.model.ModuleEntity;
import com.erapulus.server.dto.module.ModuleResponseDto;
import com.erapulus.server.mapper.ValidatorUtils;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;

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
        assertTrue(ValidatorUtils.createValidator().validate(result).isEmpty());
    }
}