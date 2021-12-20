package com.erapulus.server.database;

import com.erapulus.server.database.model.BuildingEntity;
import com.erapulus.server.database.model.UniversityEntity;
import com.erapulus.server.database.repository.BuildingRepository;
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
class BuildingRepositoryTest {

    private final static String UNIVERSITY_1 = "university1";
    private final static String UNIVERSITY_2 = "university2";
    private final static String BUILDING_1 = "building1";
    private final static String BUILDING_2 = "building2";

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @AfterEach
    void clean() {
        buildingRepository.deleteAll().block();
        universityRepository.deleteAll().block();
    }

    @Test
    void findByUniversityId_shouldReturnBuildingWhenUniversityFound() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var building1 = createBuilding(BUILDING_1, university1);
        var building2 = createBuilding(BUILDING_2, university1);
        var building3 = createBuilding(BUILDING_1, university2);
        var building4 = createBuilding(BUILDING_2, university2);

        // when
        Flux<BuildingEntity> result = buildingRepository.findAllByUniversityId(university1.id());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(posts -> posts.stream().map(BuildingEntity::id).toList().size() == 2)
                    .expectRecordedMatches(posts -> posts.stream().map(BuildingEntity::id).toList().containsAll(List.of(building1.id(), building2.id())))
                    .verifyComplete();
    }

    private BuildingEntity createBuilding(String name, UniversityEntity university) {
        BuildingEntity buildingEntity = BuildingEntity.builder()
                                                      .name(name)
                                                      .abbrev("abbrev")
                                                      .latitude(10.0)
                                                      .longitude(10.0)
                                                      .universityId(university.id())
                                                      .build();
        return buildingRepository.save(buildingEntity).block();
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
