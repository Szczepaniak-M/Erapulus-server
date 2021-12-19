package com.erapulus.server.security;

import com.erapulus.server.database.model.StudentEntity;
import com.erapulus.server.dto.StudentLoginDTO;
import com.erapulus.server.service.exception.InvalidTokenException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.webtoken.JsonWebSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleTokenValidatorTest {

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Smith";
    private static final String EMAIL = "example@gmail.com";
    private static final String PICTURE_URL = "https://url.com";
    private static final String TOKEN = "token.token.token";

    @Mock
    private GoogleIdTokenVerifier googleIdTokenVerifier;

    private GoogleTokenValidator googleTokenValidator;

    @BeforeEach
    void setUp() {
        googleTokenValidator = new GoogleTokenValidator(googleIdTokenVerifier);
    }

    @Test
    void verify_shouldReturnStudentEntityForGivenValidToken() throws GeneralSecurityException, IOException {
        // given
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.set("given_name", FIRST_NAME);
        payload.set("family_name", LAST_NAME);
        payload.set("picture", PICTURE_URL);
        payload.setEmail(EMAIL);
        GoogleIdToken token = new GoogleIdToken(new JsonWebSignature.Header(), payload, new byte[]{}, new byte[]{});
        when(googleIdTokenVerifier.verify(anyString())).thenReturn(token);
        StudentLoginDTO loginDTO = new StudentLoginDTO(TOKEN);

        // when
        Mono<StudentEntity> result = googleTokenValidator.validate(loginDTO);

        // result
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(studentFromToken -> {
                        assertEquals(EMAIL, studentFromToken.email());
                        assertEquals(FIRST_NAME, studentFromToken.firstName());
                        assertEquals(LAST_NAME, studentFromToken.lastName());
                        assertEquals(PICTURE_URL, studentFromToken.pictureUrl());
                    })
                    .verifyComplete();
    }

    @Test
    void verify_shouldReturnInvalidTokenWhenVerifyReturnNull() throws GeneralSecurityException, IOException {
        // given
        when(googleIdTokenVerifier.verify(anyString())).thenReturn(null);
        StudentLoginDTO loginDTO = new StudentLoginDTO(TOKEN);

        // when
        Mono<StudentEntity> result = googleTokenValidator.validate(loginDTO);

        // result
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(InvalidTokenException.class)
                    .verify();
    }

    @Test
    void verify_shouldReturnInvalidTokenWhenVerifyReturnException() throws GeneralSecurityException, IOException {
        // given
        when(googleIdTokenVerifier.verify(anyString())).thenThrow(new IllegalArgumentException());
        StudentLoginDTO loginDTO = new StudentLoginDTO(TOKEN);

        // when
        Mono<StudentEntity> result = googleTokenValidator.validate(loginDTO);

        // result
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(InvalidTokenException.class)
                    .verify();
    }
}
