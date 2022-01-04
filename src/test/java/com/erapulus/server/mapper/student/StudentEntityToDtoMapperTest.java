package com.erapulus.server.mapper.student;

import com.erapulus.server.database.model.StudentEntity;
import com.erapulus.server.dto.student.StudentResponseDto;
import com.erapulus.server.mapper.ValidatorUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentEntityToDtoMapperTest {
    private static final String EMAIL = "example@gmail.com";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "firstName";
    private static final String FACEBOOK_URL = "facebookUrl";
    private static final String INSTAGRAM_USERNAME = "instagramUsername";
    private static final String WHATS_UP_URL = "whatsUpUrl";
    private static final String PHONE_NUMBER = "+48 123 456 789";
    private static final String PICTURE_URL = "https://www.example.com";
    private static final Integer ID = 1;
    private static final Integer UNIVERSITY_ID = 2;

    @Test
    void from_shouldMapEntityToDto() {
        // given
        StudentEntity entity = StudentEntity.builder()
                                            .id(ID)
                                            .email(EMAIL)
                                            .firstName(FIRST_NAME)
                                            .lastName(LAST_NAME)
                                            .phoneNumber(PHONE_NUMBER)
                                            .universityId(UNIVERSITY_ID)
                                            .facebookUrl(FACEBOOK_URL)
                                            .instagramUsername(INSTAGRAM_USERNAME)
                                            .whatsUpUrl(WHATS_UP_URL)
                                            .pictureUrl(PICTURE_URL)
                                            .build();

        // when
        StudentResponseDto result = new StudentEntityToDtoMapper().from(entity);

        //then
        assertEquals(ID, result.id());
        assertEquals(EMAIL, result.email());
        assertEquals(FIRST_NAME, result.firstName());
        assertEquals(LAST_NAME, result.lastName());
        assertEquals(PHONE_NUMBER, result.phoneNumber());
        assertEquals(FACEBOOK_URL, result.facebookUrl());
        assertEquals(INSTAGRAM_USERNAME, result.instagramUsername());
        assertEquals(WHATS_UP_URL, result.whatsUpUrl());
        assertEquals(UNIVERSITY_ID, result.universityId());
        assertEquals(PICTURE_URL, result.pictureUrl());
        assertTrue(ValidatorUtils.createValidator().validate(result).isEmpty());
    }
}
