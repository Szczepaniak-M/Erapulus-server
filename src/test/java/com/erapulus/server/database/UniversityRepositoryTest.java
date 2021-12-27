package com.erapulus.server.database;

import com.erapulus.server.database.model.UniversityEntity;
import com.erapulus.server.database.repository.UniversityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class UniversityRepositoryTest {

    private final static String UNIVERSITY_1 = "university1";
    private final static String UNIVERSITY_2 = "university2";

    @Autowired
    private UniversityRepository universityRepository;

    @AfterEach
    void clean() {
        universityRepository.deleteAll().block();
    }

    @Test
    void findByUniversityId_shouldReturnBuildingWhenUniversityFound() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);

        // when
        Flux<UniversityEntity> result = universityRepository.findAllUniversities();

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(universities -> universities.stream().map(UniversityEntity::id).toList().size() == 2)
                    .expectRecordedMatches(universities -> universities.stream().map(UniversityEntity::id).toList().containsAll(List.of(university1.id(), university2.id())))
                    .verifyComplete();
    }

    private UniversityEntity createUniversity(String name) {
        UniversityEntity universityEntity = UniversityEntity.builder()
                                                            .name(name)
                                                            .address("Some address")
                                                            .zipcode("00000")
                                                            .city("city")
                                                            .country("country")
                                                            .websiteUrl("url")
                                                            .build();
        return universityRepository.save(universityEntity).block();
    }
}
