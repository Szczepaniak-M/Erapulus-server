package com.erapulus.server.document.mapper;

import com.erapulus.server.TestUtils;
import com.erapulus.server.document.database.DocumentEntity;
import com.erapulus.server.document.dto.DocumentRequestDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DocumentRequestDtoToEntityMapperTest {

    private static final int ID = 1;
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String PATH = "path";

    @Test
    void from_shouldMapDtoToEntity() {
        // given
        DocumentRequestDto requestDto = DocumentRequestDto.builder()
                                                          .name(NAME)
                                                          .description(DESCRIPTION)
                                                          .build();

        // when
        DocumentEntity result = new DocumentRequestDtoToEntityMapper().from(requestDto);

        //then
        assertNull(result.id());
        assertEquals(NAME, result.name());
        assertEquals(DESCRIPTION, result.description());
        assertNull(result.path());
        assertNull(result.universityId());
        assertNull(result.programId());
        assertNull(result.moduleId());
        assertThat(TestUtils.getValidationResult(result)).containsExactlyInAnyOrderElementsOf(List.of("id", "path"));
    }
}