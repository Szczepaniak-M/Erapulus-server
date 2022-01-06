package com.erapulus.server.security;

import com.erapulus.server.common.exception.InvalidTokenException;
import com.erapulus.server.student.database.StudentEntity;
import com.erapulus.server.student.dto.StudentLoginDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FacebookTokenValidatorTest {

    private static final String TOKEN = "token.token.token";
    private static MockWebServer mockBackEnd;
    private FacebookTokenValidator facebookTokenValidator;

    @BeforeAll
    static void initialize() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void setUp() {
        String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        facebookTokenValidator = new FacebookTokenValidator(baseUrl);
    }

    @Test
    void validate_shouldReturnStudentEntityWhenTokenCorrect() {
        StudentLoginDTO loginDTO = new StudentLoginDTO(TOKEN);
        String responseBody = """
                {
                  "first_name":"firstName",
                  "last_name":"lastName",
                  "email":"example@gmail.com",
                  "picture": {
                    "data": {
                        "height": 720,
                        "is_silhouette": false,
                        "url": "https://example.com",
                        "width": 720
                    }
                  },
                  "id": "12345"
                }""";
        mockBackEnd.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        Mono<StudentEntity> result = facebookTokenValidator.validate(loginDTO);

        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(studentFromToken -> {
                        assertEquals("example@gmail.com", studentFromToken.email());
                        assertEquals("firstName", studentFromToken.firstName());
                        assertEquals("lastName", studentFromToken.lastName());
                        assertEquals("https://example.com", studentFromToken.pictureUrl());
                    })
                    .verifyComplete();
    }

    @Test
    void validate_shouldReturnInvalidTokenExceptionWhenVerifyReturnNull() {
        StudentLoginDTO loginDTO = new StudentLoginDTO(TOKEN);
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json"));

        Mono<StudentEntity> result = facebookTokenValidator.validate(loginDTO);

        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(InvalidTokenException.class)
                    .verify();
    }

}
