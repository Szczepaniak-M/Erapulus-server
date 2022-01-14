package com.erapulus.server.friendship.web;

import com.erapulus.server.TestUtils;
import com.erapulus.server.common.web.PageablePayload;
import com.erapulus.server.friendship.database.FriendshipStatus;
import com.erapulus.server.friendship.dto.FriendshipDecisionDto;
import com.erapulus.server.friendship.dto.FriendshipRequestDto;
import com.erapulus.server.friendship.dto.FriendshipResponseDto;
import com.erapulus.server.friendship.service.FriendshipService;
import com.erapulus.server.student.dto.StudentListDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {FriendshipRouter.class, FriendshipController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class FriendshipControllerTest {

    private final static int FRIEND_ID_1 = 1;
    private final static int FRIEND_ID_2 = 2;
    private final static int STUDENT_ID = 3;

    @Autowired
    ApplicationContext applicationContext;

    @MockBean
    FriendshipService friendshipService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    void listFriends_shouldReturnFriends() {
        // given
        var friendList = createPageableFriendList();
        String expectedPayload = """
                {
                   "content":[
                     {
                        "id":1,
                        "firstName":"firstName",
                        "lastName":"lastName",
                        "pictureUrl":"pictureUrl"
                     },
                     {
                        "id":2,
                        "firstName":"firstName",
                        "lastName":"lastName",
                        "pictureUrl":"pictureUrl"
                     }
                   ],
                   "currentPage":1,
                   "totalCount":12,
                   "pageSize":10,
                   "offset":10
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(friendshipService.listFriends(eq(STUDENT_ID), eq("name"), any(PageRequest.class)))
                .thenReturn(Mono.just(friendList));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend")
                             .queryParam("name", "name")
                             .queryParam("page", "1")
                             .queryParam("pageSize", "10")
                             .build(STUDENT_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void listFriends_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(friendshipService.listFriends(eq(STUDENT_ID), eq("name"), any(PageRequest.class)))
                .thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend")
                             .queryParam("name", "name")
                             .queryParam("page", "1")
                             .queryParam("pageSize", "10")
                             .build(STUDENT_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void listFriendRequests_shouldReturnBuildings() {
        // given
        var buildingList = List.of(createStudentListDto(FRIEND_ID_1), createStudentListDto(FRIEND_ID_2));
        String expectedPayload = """
                [
                    {
                        "id":1,
                        "firstName":"firstName",
                        "lastName":"lastName",
                        "pictureUrl":"pictureUrl"
                     },
                     {
                        "id":2,
                        "firstName":"firstName",
                        "lastName":"lastName",
                        "pictureUrl":"pictureUrl"
                     }
                ]""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(friendshipService.listFriendRequests(STUDENT_ID)).thenReturn(Mono.just(buildingList));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend/request")
                             .build(STUDENT_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void listFriendRequests_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(friendshipService.listFriendRequests(STUDENT_ID)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend/request")
                             .build(STUDENT_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void addFriend_shouldReturnFriendRequestWhenDataCorrect() {
        // given
        var friendshipRequestDto = new FriendshipRequestDto(FRIEND_ID_1);
        var buildingResponseDto = createFriendshipResponseDto(STUDENT_ID, FRIEND_ID_1, FriendshipStatus.REQUESTED);
        String expectedPayload = """
                {
                   "id":1,
                   "applicationUserId":1,
                   "friendId":3,
                   "status":"REQUESTED"
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(friendshipService.addFriendRequest(any(FriendshipRequestDto.class), eq(STUDENT_ID)))
                .thenReturn(Mono.just(buildingResponseDto));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend")
                             .build(STUDENT_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(friendshipRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void addFriend_shouldReturnBadRequestWhenMissingField() {
        // given
        var friendshipRequestDto = new FriendshipRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;userId.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(friendshipService.addFriendRequest(any(FriendshipRequestDto.class), eq(STUDENT_ID)))
                .thenThrow(new ConstraintViolationException(validator.validate(friendshipRequestDto)));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend")
                             .build(STUDENT_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(friendshipRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void addFriend_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(friendshipService.addFriendRequest(any(FriendshipRequestDto.class), eq(STUDENT_ID)))
                .thenThrow(new IllegalStateException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend")
                             .build(STUDENT_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void addFriend_shouldReturnBadRequestWhenRequestedYourself() {
        // given
        var friendshipRequestDto = new FriendshipRequestDto(STUDENT_ID);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;cant.invite.yourself");
        when(friendshipService.addFriendRequest(any(FriendshipRequestDto.class), eq(STUDENT_ID)))
                .thenThrow(new IllegalArgumentException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend")
                             .build(STUDENT_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(friendshipRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void addFriend_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        var friendshipRequestDto = new FriendshipRequestDto(FRIEND_ID_1);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "friend.not.found");
        when(friendshipService.addFriendRequest(any(FriendshipRequestDto.class), eq(STUDENT_ID)))
                .thenReturn(Mono.error(new NoSuchElementException("friend")));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend")
                             .build(STUDENT_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(friendshipRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void addFriend_shouldReturnConflictWhenFriendAcceptedDuplicated() {
        // given
        var friendshipRequestDto = new FriendshipRequestDto(FRIEND_ID_1);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "duplicated.request.conflict");
        when(friendshipService.addFriendRequest(any(FriendshipRequestDto.class), eq(STUDENT_ID)))
                .thenThrow(new DuplicateKeyException("building"));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend")
                             .build(STUDENT_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(friendshipRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void addFriend_shouldReturnConflictWhenRequestDuplicated() {
        // given
        var friendshipRequestDto = new FriendshipRequestDto(FRIEND_ID_1);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "other.request.conflict");
        when(friendshipService.addFriendRequest(any(FriendshipRequestDto.class), eq(STUDENT_ID)))
                .thenThrow(new IllegalStateException("building"));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend")
                             .build(STUDENT_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(friendshipRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void addFriend_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var friendshipRequestDto = new FriendshipRequestDto(FRIEND_ID_1);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(friendshipService.addFriendRequest(any(FriendshipRequestDto.class), eq(STUDENT_ID)))
                .thenThrow(new RuntimeException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend")
                             .build(STUDENT_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(friendshipRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void handleFriendshipRequest_shouldReturnFriendRequestWhenDataCorrect() {
        // given
        var friendshipDecisionDto = new FriendshipDecisionDto(true);
        var buildingResponseDto = createFriendshipResponseDto(FRIEND_ID_1, STUDENT_ID, FriendshipStatus.ACCEPTED);
        String expectedPayload = """
                {
                   "id":1,
                   "applicationUserId":3,
                   "friendId":1,
                   "status":"ACCEPTED"
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(friendshipService.handleFriendshipRequest(any(FriendshipDecisionDto.class), eq(STUDENT_ID), eq(FRIEND_ID_1)))
                .thenReturn(Mono.just(buildingResponseDto));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend/{friendId}")
                             .build(STUDENT_ID, FRIEND_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(friendshipDecisionDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void handleFriendshipRequest_shouldReturnBadRequestWhenMissingField() {
        // given
        var friendshipDecisionDto = new FriendshipDecisionDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;accept.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(friendshipService.handleFriendshipRequest(any(FriendshipDecisionDto.class), eq(STUDENT_ID), eq(FRIEND_ID_1)))
                .thenThrow(new ConstraintViolationException(validator.validate(friendshipDecisionDto)));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend/{friendId}")
                             .build(STUDENT_ID, FRIEND_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(friendshipDecisionDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void handleFriendshipRequest_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(friendshipService.handleFriendshipRequest(any(FriendshipDecisionDto.class), eq(STUDENT_ID), eq(FRIEND_ID_1)))
                .thenThrow(new IllegalStateException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend/{friendId}")
                             .build(STUDENT_ID, FRIEND_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void handleFriendshipRequest_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        var friendshipDecisionDto = new FriendshipDecisionDto(true);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "friend.not.found");
        when(friendshipService.handleFriendshipRequest(any(FriendshipDecisionDto.class), eq(STUDENT_ID), eq(FRIEND_ID_1)))
                .thenReturn(Mono.error(new NoSuchElementException("friend")));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend/{friendId}")
                             .build(STUDENT_ID, FRIEND_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(friendshipDecisionDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void handleFriendshipRequest_shouldReturnConflictWhenFriendDuplicated() {
        // given
        var friendshipDecisionDto = new FriendshipDecisionDto(true);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "friend.conflict");
        when(friendshipService.handleFriendshipRequest(any(FriendshipDecisionDto.class), eq(STUDENT_ID), eq(FRIEND_ID_1)))
                .thenThrow(new DuplicateKeyException("friend"));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend/{friendId}")
                             .build(STUDENT_ID, FRIEND_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(friendshipDecisionDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void handleFriendshipRequest_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var friendshipDecisionDto = new FriendshipDecisionDto(true);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(friendshipService.handleFriendshipRequest(any(FriendshipDecisionDto.class), eq(STUDENT_ID), eq(FRIEND_ID_1)))
                .thenThrow(new RuntimeException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend/{friendId}")
                             .build(STUDENT_ID, FRIEND_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(friendshipDecisionDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteFriend_shouldReturnNoContent() {
        // given
        String expectedPayload = null;
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.NO_CONTENT.value(), expectedPayload);
        when(friendshipService.deleteFriend(STUDENT_ID, FRIEND_ID_1)).thenReturn(Mono.just(true));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend/{friendId}")
                             .build(STUDENT_ID, FRIEND_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NO_CONTENT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteFriend_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "friend.not.found");
        when(friendshipService.deleteFriend(STUDENT_ID, FRIEND_ID_1)).thenReturn(Mono.error(new NoSuchElementException("friend")));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend/{friendId}")
                             .build(STUDENT_ID, FRIEND_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteFriend_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(friendshipService.deleteFriend(STUDENT_ID, FRIEND_ID_1)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/friend/{friendId}")
                             .build(STUDENT_ID, FRIEND_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    private PageablePayload<StudentListDto> createPageableFriendList() {
        return new PageablePayload<>(List.of(createStudentListDto(FRIEND_ID_1), createStudentListDto(FRIEND_ID_2)),
                PageRequest.of(1, 10), 12);
    }

    private StudentListDto createStudentListDto(int id) {
        return StudentListDto.builder()
                             .id(id)
                             .firstName("firstName")
                             .lastName("lastName")
                             .pictureUrl("pictureUrl")
                             .build();
    }

    private FriendshipResponseDto createFriendshipResponseDto(int studentId, int friendId, FriendshipStatus status) {
        return FriendshipResponseDto.builder()
                                    .id(1)
                                    .applicationUserId(friendId)
                                    .friendId(studentId)
                                    .status(status)
                                    .build();
    }
}

