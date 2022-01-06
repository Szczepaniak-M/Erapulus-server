package com.erapulus.server.document.mapper;

import com.erapulus.server.TestUtils;
import com.erapulus.server.document.database.DocumentEntity;
import com.erapulus.server.document.dto.DocumentResponseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentEntityToResponseDtoMapperTest {

    private static final int ID = 1;
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String PATH = "path";
    private static final int UNIVERSITY_ID = 2;
    private static final int PROGRAM_ID = 3;
    private static final int MODULE_ID = 4;

    @Test
    void from_shouldMapEntityToDto() {
        // given
        DocumentEntity entity = DocumentEntity.builder()
                                              .id(ID)
                                              .name(NAME)
                                              .description(DESCRIPTION)
                                              .path(PATH)
                                              .universityId(UNIVERSITY_ID)
                                              .programId(PROGRAM_ID)
                                              .moduleId(MODULE_ID)
                                              .build();

        // when
        DocumentResponseDto result = new DocumentEntityToResponseDtoMapper().from(entity);

        //then
        assertEquals(ID, result.id());
        assertEquals(NAME, result.name());
        assertEquals(DESCRIPTION, result.description());
        assertEquals(PATH, result.path());
        assertEquals(UNIVERSITY_ID, result.universityId());
        assertEquals(PROGRAM_ID, result.programId());
        assertEquals(MODULE_ID, result.moduleId());
        assertTrue(TestUtils.createValidator().validate(result).isEmpty());
    }
}