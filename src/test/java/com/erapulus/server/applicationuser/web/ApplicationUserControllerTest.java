package com.erapulus.server.applicationuser.web;

import com.erapulus.server.TestUtils;
import com.erapulus.server.applicationuser.dto.ApplicationUserDto;
import com.erapulus.server.applicationuser.service.ApplicationUserService;
import com.erapulus.server.common.database.UserType;
import com.erapulus.server.common.web.PageablePayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {ApplicationUserRouter.class, ApplicationUserController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class ApplicationUserControllerTest {

    private static final int USER_ID = 1;

    @Autowired
    ApplicationContext applicationContext;

    @MockBean
    ApplicationUserService applicationUserService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    void listApplicationUsers_shouldReturnApplicationUsers() {
        // given
        var pageableApplicationUsersList = createPageableApplicationUsersList();
        String expectedPayload = """
                {
                  "content":[
                    {
                      "id":1,
                      "type":"EMPLOYEE",
                      "firstName":"firstName",
                      "lastName":"lastName",
                      "universityId":3,
                      "email":"example@gmail.com"
                    },
                    {
                      "id":2,
                      "type":"EMPLOYEE",
                      "firstName":"firstName",
                      "lastName":"lastName",
                      "universityId":3,
                      "email":"example@gmail.com"
                    }
                  ],
                  "currentPage":1,
                  "totalCount":12,
                  "pageSize":10,
                  "offset":10
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(applicationUserService.listApplicationUsers(eq("3"), eq("EMPLOYEE"), eq("name"), eq("example@gmail.com"), any(PageRequest.class)))
                .thenReturn(Mono.just(pageableApplicationUsersList));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/user")
                             .queryParam("university", "3")
                             .queryParam("type", "EMPLOYEE")
                             .queryParam("name", "name")
                             .queryParam("email", "example@gmail.com")
                             .queryParam("page", "1")
                             .queryParam("pageSize", "10")
                             .build())
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void listApplicationUsers_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(applicationUserService.listApplicationUsers(eq("3"), eq("EMPLOYEE"), eq("name"), eq("example@gmail.com"), any(PageRequest.class)))
                .thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/user")
                             .queryParam("university", "3")
                             .queryParam("type", "EMPLOYEE")
                             .queryParam("name", "name")
                             .queryParam("email", "example@gmail.com")
                             .queryParam("page", "1")
                             .queryParam("pageSize", "10")
                             .build())
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteApplicationUser_shouldReturnNoContent() {
        // given
        String expectedPayload = null;
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.NO_CONTENT.value(), expectedPayload);
        when(applicationUserService.deleteApplicationUser(USER_ID)).thenReturn(Mono.just(true));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/user/{userId}")
                             .build(USER_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NO_CONTENT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteApplicationUser_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "user.not.found");
        when(applicationUserService.deleteApplicationUser(USER_ID)).thenReturn(Mono.error(new NoSuchElementException("user")));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/user/{userId}")
                             .build(USER_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteApplicationUser_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(applicationUserService.deleteApplicationUser(USER_ID))
                .thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/user/{userId}")
                             .build(USER_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    private PageablePayload<ApplicationUserDto> createPageableApplicationUsersList() {
        return new PageablePayload<>(List.of(createApplicationUser(1), createApplicationUser(2)),
                PageRequest.of(1, 10), 12);
    }

    private ApplicationUserDto createApplicationUser(int id) {
        return ApplicationUserDto.builder()
                                 .id(id)
                                 .type(UserType.EMPLOYEE)
                                 .firstName("firstName")
                                 .lastName("lastName")
                                 .universityId(3)
                                 .email("example@gmail.com")
                                 .build();
    }
}

