package com.erapulus.server.faculty.mapper;

import com.erapulus.server.TestUtils;
import com.erapulus.server.faculty.database.FacultyEntity;
import com.erapulus.server.faculty.dto.FacultyResponseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FacultyEntityToResponseDtoMapperTest {

    private static final int ID = 1;
    private static final String NAME = "name";
    private static final String EMAIL = "example@gmail.com";
    private static final String ADDRESS = "address";
    private static final Integer UNIVERSITY_ID = 2;

    @Test
    void from_shouldMapEntityToDto() {
        // given
        FacultyEntity entity = FacultyEntity.builder()
                                            .id(ID)
                                            .name(NAME)
                                            .address(ADDRESS)
                                            .email(EMAIL)
                                            .universityId(UNIVERSITY_ID)
                                            .build();

        // when
        FacultyResponseDto result = new FacultyEntityToResponseDtoMapper().from(entity);

        //then
        assertEquals(ID, result.id());
        assertEquals(NAME, result.name());
        assertEquals(EMAIL, result.email());
        assertEquals(ADDRESS, result.address());
        assertEquals(UNIVERSITY_ID, result.universityId());
        assertTrue(TestUtils.createValidator().validate(result).isEmpty());
    }
}