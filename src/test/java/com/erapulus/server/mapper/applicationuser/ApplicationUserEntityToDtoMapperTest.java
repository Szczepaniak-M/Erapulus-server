package com.erapulus.server.mapper.applicationuser;

import com.erapulus.server.database.model.ApplicationUserEntity;
import com.erapulus.server.database.model.UserType;
import com.erapulus.server.dto.applicationuser.ApplicationUserDto;
import com.erapulus.server.mapper.ValidatorUtils;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationUserEntityToDtoMapperTest {

    private static final int ID = 1;
    private static final UserType USER_TYPE = UserType.STUDENT;
    private static final String EMAIL = "example@gmail.com";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "firstName";
    private static final Integer UNIVERSITY_ID = 2;

    @Test
    void from_shouldMapEntityToDto() {
        // given
        ApplicationUserEntity entity = ApplicationUserEntity.builder()
                                                            .id(ID)
                                                            .type(USER_TYPE)
                                                            .firstName(FIRST_NAME)
                                                            .lastName(LAST_NAME)
                                                            .email(EMAIL)
                                                            .universityId(UNIVERSITY_ID)
                                                            .build();

        // when
        ApplicationUserDto result = new ApplicationUserEntityToDtoMapper().from(entity);

        //then
        assertEquals(ID, result.id());
        assertEquals(USER_TYPE, result.type());
        assertEquals(FIRST_NAME, result.firstName());
        assertEquals(LAST_NAME, result.lastName());
        assertEquals(EMAIL, result.email());
        assertEquals(UNIVERSITY_ID, result.universityId());
        assertTrue(ValidatorUtils.createValidator().validate(result).isEmpty());
    }
}
