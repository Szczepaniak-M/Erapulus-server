package com.erapulus.server.database;

import com.erapulus.server.database.model.PostEntity;
import com.erapulus.server.database.repository.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import com.erapulus.server.database.model.UniversityEntity;
import com.erapulus.server.database.repository.UniversityRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PostRepositoryTest {

    public static final String TITLE_1 = "First Amazing Post";
    public static final String TITLE_2 = "Second Post";
    public static final String TITLE_3 = "Third Amazing Post";
    public static final String TITLE_PART = "amazing";
    private static final String UNIVERSITY_1 = "university1";
    private static final String UNIVERSITY_2 = "university2";
    private static final LocalDate DATE_1 = LocalDate.of(1970, 1, 1);
    private static final LocalDate DATE_2 = LocalDate.of(1970, 1, 2);
    private static final LocalDate DATE_3 = LocalDate.of(1970, 1, 3);
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @AfterEach
    void clean() {
        postRepository.deleteAll().block();
        universityRepository.deleteAll().block();
    }

    @Test
    void findPostByFilters_shouldReturnPostEntityWhenFriendsFound() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var post1 = createPost(TITLE_1, DATE_1, university);
        var post2 = createPost(TITLE_2, DATE_1, university);
        var pageRequest = PageRequest.of(0, 1);

        // when
        var result = postRepository.findPostByFilters(university.id(), TITLE_PART, DATE_1, DATE_1, pageRequest.getOffset(), pageRequest.getPageSize());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(post -> Assertions.assertEquals(post1.id(), post.id()))
                    .verifyComplete();
    }

    @Test
    void findPostByFilters_shouldReturnPostEntityWhenDatesGivenFound() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var post1 = createPost(TITLE_1, DATE_1, university);
        var post2 = createPost(TITLE_2, DATE_2, university);
        var post3 = createPost(TITLE_3, DATE_3, university);
        var pageRequest = PageRequest.of(0, 2);

        // when
        var result = postRepository.findPostByFilters(university.id(), null, DATE_2, DATE_3, pageRequest.getOffset(), pageRequest.getPageSize());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(posts -> posts.stream().map(PostEntity::id).toList().containsAll(List.of(post2.id(), post3.id())))
                    .verifyComplete();
    }

    @Test
    void findPostByFilters_shouldReturnModulesWhenSecondPageRequested() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var post1 = createPost(TITLE_1, DATE_1, university1);
        var post2 = createPost(TITLE_2, DATE_2, university1);
        var post3 = createPost(TITLE_3, DATE_3, university1);
        var post4 = createPost(TITLE_1, DATE_1, university2);
        var post5 = createPost(TITLE_2, DATE_2, university2);
        var post6 = createPost(TITLE_3, DATE_3, university2);
        var pageRequest = PageRequest.of(1, 1);

        // when
        var result = postRepository.findPostByFilters(university2.id(), null, DATE_1, DATE_3, pageRequest.getOffset(), pageRequest.getPageSize());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(post -> assertEquals(post5.id(), post.id()))
                    .verifyComplete();
    }

    @Test
    void countPostByFilters_shouldReturnPostNumberForGivenUniversity() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var post1 = createPost(TITLE_1, DATE_1, university1);
        var post2 = createPost(TITLE_2, DATE_2, university1);
        var post3 = createPost(TITLE_3, DATE_2, university1);
        var post4 = createPost(TITLE_1, DATE_3, university1);
        var post5 = createPost(TITLE_1, DATE_3, university2);
        int expectedResult = 2;

        // when
        var result = postRepository.countPostByFilters(university1.id(), TITLE_PART, DATE_2, DATE_3);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(postCount -> assertEquals(expectedResult, postCount))
                    .verifyComplete();
    }

    @Test
    void findByIdAndUniversityId_shouldReturnPostWhenUniversityAndIdExists() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var post1 = createPost(TITLE_1, DATE_1, university1);
        var post2 = createPost(TITLE_2, DATE_1, university1);
        var post3 = createPost(TITLE_1, DATE_1, university2);
        var post4 = createPost(TITLE_2, DATE_1, university2);

        // when
        Mono<PostEntity> result = postRepository.findByIdAndUniversityId(post1.id(), university1.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(facultyFromDatabase -> assertEquals(post1.id(), facultyFromDatabase.id()))
                    .verifyComplete();
    }

    @Test
    void findByIdAndUniversityId_shouldReturnEmptyMonoWhenWrongUniversity() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var post1 = createPost(TITLE_1, DATE_1, university1);
        var post2 = createPost(TITLE_1, DATE_1, university2);

        // when
        Mono<PostEntity> result = postRepository.findByIdAndUniversityId(post1.id(), university2.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    private UniversityEntity createUniversity(String name) {
        var universityEntity = UniversityEntity.builder()
                                               .name(name)
                                               .address("Some address")
                                               .zipcode("00000")
                                               .city("city")
                                               .country("country")
                                               .websiteUrl("url")
                                               .build();
        return universityRepository.save(universityEntity).block();
    }

    private PostEntity createPost(String title, LocalDate date, UniversityEntity universityEntity) {
        var postEntity = PostEntity.builder()
                                   .title(title)
                                   .universityId(universityEntity.id())
                                   .date(date)
                                   .content("Content")
                                   .build();
        return postRepository.save(postEntity).block();
    }
}
