package com.erapulus.server.post.web;

import com.erapulus.server.TestUtils;
import com.erapulus.server.common.web.PageablePayload;
import com.erapulus.server.post.dto.PostRequestDto;
import com.erapulus.server.post.dto.PostResponseDto;
import com.erapulus.server.post.service.PostService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {PostRouter.class, PostController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class PostControllerTest {

    private final static int POST_ID_1 = 1;
    private final static int POST_ID_2 = 2;
    private final static int UNIVERSITY_ID = 3;

    @Autowired
    ApplicationContext applicationContext;

    @MockBean
    PostService postService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    void listPosts_shouldReturnPosts() {
        // given
        var postList = createPageablePostList();
        String expectedPayload = """
                {
                   "content":[
                     {
                       "id":1,
                       "title":"title",
                       "date":"2021-12-31",
                       "content":"content",
                       "universityId":3
                     },
                     {
                       "id":2,
                       "title":"title",
                       "date":"2021-12-31",
                       "content":"content",
                       "universityId":3
                     }
                   ],
                   "currentPage":1,
                   "totalCount":12,
                   "pageSize":10,
                   "offset":10
                 }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(postService.listPosts(eq(UNIVERSITY_ID), eq("title"), eq("2021-12-01"), eq("2021-12-31"), any(PageRequest.class)))
                .thenReturn(Mono.just(postList));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post")
                             .queryParam("title", "title")
                             .queryParam("from", "2021-12-01")
                             .queryParam("to", "2021-12-31")
                             .queryParam("page", "1")
                             .queryParam("pageSize", "10")
                             .build(UNIVERSITY_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void listPosts_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(postService.listPosts(eq(UNIVERSITY_ID), eq("title"), eq("2021-12-01"), eq("2021-12-31"), any(PageRequest.class)))
                .thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post")
                             .queryParam("title", "title")
                             .queryParam("from", "2021-12-01")
                             .queryParam("to", "2021-12-31")
                             .queryParam("page", "1")
                             .queryParam("pageSize", "10")
                             .build(UNIVERSITY_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createPost_shouldReturnCreatedPostWhenDataCorrect() {
        // given
        var postRequestDto = createPostRequestDto();
        var postResponseDto = createPostResponseDto(POST_ID_1);
        String expectedPayload = """
                {
                   "id":1,
                   "title":"title",
                   "date":"2021-12-31",
                   "content":"content",
                   "universityId":3
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.CREATED.value(), expectedPayload);
        when(postService.createPost(any(PostRequestDto.class), eq(UNIVERSITY_ID))).thenReturn(Mono.just(postResponseDto));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post")
                             .build(UNIVERSITY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(postRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CREATED)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createPost_shouldReturnBadRequestWhenMissingField() {
        // given
        var postRequestDto = createPostRequestDto().title(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;title.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(postService.createPost(any(PostRequestDto.class), eq(UNIVERSITY_ID)))
                .thenThrow(new ConstraintViolationException(validator.validate(postRequestDto)));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post")
                             .build(UNIVERSITY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(postRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createPost_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(postService.createPost(any(PostRequestDto.class), eq(UNIVERSITY_ID))).thenThrow(new IllegalStateException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post")
                             .build(UNIVERSITY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createPost_shouldReturnConflictWhenPostDuplicated() {
        // given
        var postRequestDto = createPostRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "post.conflict");
        when(postService.createPost(any(PostRequestDto.class), eq(UNIVERSITY_ID))).thenThrow(new DuplicateKeyException("post"));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post")
                             .build(UNIVERSITY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(postRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createPost_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var postRequestDto = createPostRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(postService.createPost(any(PostRequestDto.class), eq(UNIVERSITY_ID))).thenThrow(new RuntimeException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post")
                             .build(UNIVERSITY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(postRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getPostById_shouldReturnPost() {
        // given
        var postResponseDto = createPostResponseDto(POST_ID_1);
        String expectedPayload = """
                {
                   "id":1,
                   "title":"title",
                   "date":"2021-12-31",
                   "content":"content",
                   "universityId":3
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(postService.getPostById(POST_ID_1, UNIVERSITY_ID)).thenReturn(Mono.just(postResponseDto));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post/{postId}")
                             .build(UNIVERSITY_ID, POST_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getPostById_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "post.not.found");
        when(postService.getPostById(POST_ID_1, UNIVERSITY_ID)).thenReturn(Mono.error(new NoSuchElementException("post")));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post/{postId}")
                             .build(UNIVERSITY_ID, POST_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getPostById_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(postService.getPostById(POST_ID_1, UNIVERSITY_ID)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post/{postId}")
                             .build(UNIVERSITY_ID, POST_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updatePost_shouldReturnUpdatedPostWhenDataCorrect() {
        // given
        var postRequestDto = createPostRequestDto();
        var postResponseDto = createPostResponseDto(POST_ID_1);
        String expectedPayload = """
                {
                   "id":1,
                   "title":"title",
                   "date":"2021-12-31",
                   "content":"content",
                   "universityId":3
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(postService.updatePost(any(PostRequestDto.class), eq(POST_ID_1), eq(UNIVERSITY_ID)))
                .thenReturn(Mono.just(postResponseDto));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post/{postId}")
                             .build(UNIVERSITY_ID, POST_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(postRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updatePost_shouldReturnBadRequestWhenMissingField() {
        // given
        var postRequestDto = createPostRequestDto().title(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;title.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(postService.updatePost(any(PostRequestDto.class), eq(POST_ID_1), eq(UNIVERSITY_ID)))
                .thenThrow(new ConstraintViolationException(validator.validate(postRequestDto)));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post/{postId}")
                             .build(UNIVERSITY_ID, POST_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(postRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updatePost_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(postService.updatePost(any(PostRequestDto.class), eq(POST_ID_1), eq(UNIVERSITY_ID)))
                .thenThrow(new IllegalStateException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post/{postId}")
                             .build(UNIVERSITY_ID, POST_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updatePost_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        var postRequestDto = createPostRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "post.not.found");
        when(postService.updatePost(any(PostRequestDto.class), eq(POST_ID_1), eq(UNIVERSITY_ID)))
                .thenReturn(Mono.error(new NoSuchElementException("post")));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post/{postId}")
                             .build(UNIVERSITY_ID, POST_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(postRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updatePost_shouldReturnConflictWhenPostDuplicated() {
        // given
        var postRequestDto = createPostRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "post.conflict");
        when(postService.updatePost(any(PostRequestDto.class), eq(POST_ID_1), eq(UNIVERSITY_ID)))
                .thenThrow(new DuplicateKeyException("post"));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post/{postId}")
                             .build(UNIVERSITY_ID, POST_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(postRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updatePost_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var postRequestDto = createPostRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(postService.updatePost(any(PostRequestDto.class), eq(POST_ID_1), eq(UNIVERSITY_ID)))
                .thenThrow(new RuntimeException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post/{postId}")
                             .build(UNIVERSITY_ID, POST_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(postRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deletePost_shouldReturnNoContent() {
        // given
        String expectedPayload = null;
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.NO_CONTENT.value(), expectedPayload);
        when(postService.deletePost(POST_ID_1, UNIVERSITY_ID)).thenReturn(Mono.just(true));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post/{postId}")
                             .build(UNIVERSITY_ID, POST_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NO_CONTENT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deletePost_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "post.not.found");
        when(postService.deletePost(POST_ID_1, UNIVERSITY_ID)).thenReturn(Mono.error(new NoSuchElementException("post")));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post/{postId}")
                             .build(UNIVERSITY_ID, POST_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deletePost_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(postService.deletePost(POST_ID_1, UNIVERSITY_ID)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/post/{postId}")
                             .build(UNIVERSITY_ID, POST_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    private PageablePayload<PostResponseDto> createPageablePostList() {
        return new PageablePayload<>(List.of(createPostResponseDto(POST_ID_1), createPostResponseDto(POST_ID_2)),
                PageRequest.of(1, 10), 12);
    }

    private PostResponseDto createPostResponseDto(int id) {
        return PostResponseDto.builder()
                              .id(id)
                              .title("title")
                              .date(LocalDate.of(2021, 12, 31))
                              .content("content")
                              .universityId(UNIVERSITY_ID)
                              .build();
    }

    private PostRequestDto createPostRequestDto() {
        return PostRequestDto.builder()
                             .title("title")
                             .content("content")
                             .build();
    }
}

