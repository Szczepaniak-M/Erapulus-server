package com.erapulus.server.mapper;

import com.erapulus.server.database.model.StudentEntity;
import com.erapulus.server.dto.StudentResponseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StudentEntityEntityToDtoMapperTest {
    private static final String EMAIL = "example@gmail.com";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "firstName";
    private static final String FACEBOOK_URL = "facebookUrl";
    private static final String INSTAGRAM_USERNAME = "instagramUsername";
    private static final String WHATS_UP_URL = "whatsUpUrl";

    private static final Integer ID = 1;
    private static final Integer UNIVERSITY_ID = 2;

    @Test
    void from_shouldMapEntityToDto() {
        // given
        StudentEntity studentEntity = StudentEntity.builder()
                                                   .id(ID)
                                                   .email(EMAIL)
                                                   .firstName(FIRST_NAME)
                                                   .lastName(LAST_NAME)
                                                   .universityId(UNIVERSITY_ID)
                                                   .facebookUrl(FACEBOOK_URL)
                                                   .instagramUsername(INSTAGRAM_USERNAME)
                                                   .whatsUpUrl(WHATS_UP_URL)
                                                   .build();
        var studentEntityToDtoMapper = new StudentEntityToDtoMapper();

        // when
        StudentResponseDto result = studentEntityToDtoMapper.from(studentEntity);

        //then
        assertEquals(ID, result.id());
        assertEquals(EMAIL, result.email());
        assertEquals(FIRST_NAME, result.firstName());
        assertEquals(LAST_NAME, result.lastName());
        assertEquals(FACEBOOK_URL, result.facebookUrl());
        assertEquals(INSTAGRAM_USERNAME, result.instagramUsername());
        assertEquals(WHATS_UP_URL, result.whatsUpUrl());
        assertEquals(UNIVERSITY_ID, result.universityId());
    }
}
