package com.erapulus.server.post.service;


import com.erapulus.server.applicationuser.dto.ApplicationUserDto;
import com.erapulus.server.common.web.PageablePayload;
import com.erapulus.server.post.database.PostEntity;
import com.erapulus.server.post.database.PostRepository;
import com.erapulus.server.post.dto.PostRequestDto;
import com.erapulus.server.post.dto.PostResponseDto;
import com.erapulus.server.post.mapper.PostEntityToResponseDtoMapper;
import com.erapulus.server.post.mapper.PostRequestDtoToEntityMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    public static final int UNIVERSITY_ID = 1;
    private final static int ID_1 = 1;
    private final static int ID_2 = 2;

    @Mock
    PostRepository postRepository;

    PostService postService;


    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository,
                new PostRequestDtoToEntityMapper(),
                new PostEntityToResponseDtoMapper());
    }

    @Test
    void listPosts_shouldPageableListWhenCorrectInput() {
        // given
        var post1 = createPost(ID_1);
        var post2 = createPost(ID_2);
        var postDto1 = createPostResponseDto(ID_1);
        var postDto2 = createPostResponseDto(ID_2);
        var totalCount = 12;
        String fromDate = "2021-11-23";
        String toDate = "";
        String title = "";
        PageRequest pageRequest = PageRequest.of(1, 10);
        when(postRepository.findPostByFilters(UNIVERSITY_ID, title, LocalDate.of(2021,11,23), LocalDate.of(9999,12,31), pageRequest.getOffset(), pageRequest.getPageSize()))
                .thenReturn(Flux.just(post1, post2));
        when(postRepository.countPostByFilters(UNIVERSITY_ID, title, LocalDate.of(2021,11,23),  LocalDate.of(9999,12,31)))
                .thenReturn(Mono.just(totalCount));
        PageablePayload<PostResponseDto> expected = new PageablePayload<>(List.of(postDto1, postDto2), pageRequest, totalCount);

        // when
        Mono<PageablePayload<PostResponseDto>> result = postService.listPosts(UNIVERSITY_ID, title, fromDate, toDate, pageRequest);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(payload -> assertThat(payload)
                            .usingRecursiveComparison()
                            .isEqualTo(expected))
                    .verifyComplete();
    }

    @Test
    void listPosts_shouldReturnExceptionWhenWrongInputDate() {
        // given
        String fromDate = "2021-111-23";
        String toDate = "";
        String title = "";
        PageRequest pageRequest = PageRequest.of(1, 10);

        // when
        Mono<PageablePayload<PostResponseDto>> result = postService.listPosts(UNIVERSITY_ID, title, fromDate, toDate, pageRequest);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(IllegalArgumentException.class)
                    .verify();
    }

    @Test
    void createPost_shouldCreatePost() {
        // given
        var postRequestDto = new PostRequestDto();
        when(postRepository.save(any(PostEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, PostEntity.class).id(ID_1)));

        // when
        Mono<PostResponseDto> result = postService.createPost(postRequestDto, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(post -> {
                        assertEquals(ID_1, post.id());
                        assertEquals(UNIVERSITY_ID, post.universityId());
                    })
                    .verifyComplete();
    }

    @Test
    void getPostById_shouldReturnPostWhenFound() {
        // given
        var post = createPost(ID_1);
        when(postRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.just(post));

        // when
        Mono<PostResponseDto> result = postService.getPostById(ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(postResponseDto -> {
                        assertEquals(ID_1, postResponseDto.id());
                        assertEquals(UNIVERSITY_ID, postResponseDto.universityId());
                    })
                    .verifyComplete();
    }

    @Test
    void getPostById_shouldThrowExceptionWhenPostNotFound() {
        // given
        when(postRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.empty());

        // when
        Mono<PostResponseDto> result = postService.getPostById(ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void updatePost_shouldUpdatePostWhenFound() {
        // given
        var post = createPost(ID_1);
        var postRequestDto = new PostRequestDto();
        when(postRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.just(post));
        when(postRepository.save(any(PostEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, PostEntity.class).id(ID_1)));

        // when
        Mono<PostResponseDto> result = postService.updatePost(postRequestDto, ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(postResponseDto -> {
                        assertEquals(ID_1, postResponseDto.id());
                        assertEquals(UNIVERSITY_ID, postResponseDto.universityId());
                    })
                    .verifyComplete();
    }

    @Test
    void updatePost_shouldThrowExceptionWhenPostNotFound() {
        // given
        var postRequestDto = new PostRequestDto();
        when(postRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.empty());

        // when
        Mono<PostResponseDto> result = postService.updatePost(postRequestDto, ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deletePost_shouldDeletePostWhenFound() {
        // given
        var post = createPost(ID_1);
        when(postRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.just(post));
        when(postRepository.deleteById(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<Boolean> result = postService.deletePost(ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertTrue)
                    .verifyComplete();
    }

    @Test
    void deletePost_shouldThrowExceptionWhenPostNotFound() {
        // given
        when(postRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.empty());

        // when
        Mono<Boolean> result = postService.deletePost(ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteAllPostsByUniversityId() {
        // when
        when(postRepository.deleteAllByUniversityId(UNIVERSITY_ID)).thenReturn(Mono.empty());

        // given
        Mono<Void> result = postService.deleteAllPostsByUniversityId(UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    private PostEntity createPost(int id) {
        return PostEntity.builder()
                         .id(id)
                         .title("title")
                         .universityId(UNIVERSITY_ID)
                         .build();
    }

    private PostResponseDto createPostResponseDto(int id) {
        return PostResponseDto.builder()
                              .id(id)
                              .title("title")
                              .universityId(UNIVERSITY_ID)
                              .build();
    }
}