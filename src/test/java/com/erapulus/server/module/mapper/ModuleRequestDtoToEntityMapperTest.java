package com.erapulus.server.module.mapper;

import com.erapulus.server.TestUtils;
import com.erapulus.server.module.database.ModuleEntity;
import com.erapulus.server.module.dto.ModuleRequestDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ModuleRequestDtoToEntityMapperTest {

    private static final String NAME = "name";
    private static final String ABBREV = "abbrev";
    private static final String DESCRIPTION = "description";

    @Test
    void from_shouldMapDtoToEntity() {
        // given
        ModuleRequestDto requestDto = ModuleRequestDto.builder()
                                                      .name(NAME)
                                                      .abbrev(ABBREV)
                                                      .description(DESCRIPTION)
                                                      .build();

        // when
        ModuleEntity result = new ModuleRequestDtoToEntityMapper().from(requestDto);

        //then
        assertNull(result.id());
        assertEquals(NAME, result.name());
        assertEquals(ABBREV, result.abbrev());
        assertEquals(DESCRIPTION, result.description());
        assertNull(result.programId());
        assertThat(TestUtils.getValidationResult(result)).containsExactlyInAnyOrderElementsOf(List.of("id", "programId"));
    }
}