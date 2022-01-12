package com.erapulus.server.building.service;

import com.erapulus.server.building.database.BuildingEntity;
import com.erapulus.server.building.database.BuildingRepository;
import com.erapulus.server.building.dto.BuildingRequestDto;
import com.erapulus.server.building.dto.BuildingResponseDto;
import com.erapulus.server.building.mapper.BuildingEntityToResponseDtoMapper;
import com.erapulus.server.building.mapper.BuildingRequestDtoToEntityMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuildingServiceTest {

    private final static int ID_1 = 1;
    private final static int ID_2 = 2;
    private static final int UNIVERSITY_ID = 3;

    @Mock
    BuildingRepository buildingRepository;

    BuildingService buildingService;


    @BeforeEach
    void setUp() {
        buildingService = new BuildingService(buildingRepository,
                new BuildingRequestDtoToEntityMapper(),
                new BuildingEntityToResponseDtoMapper());
    }

    @Test
    void listBuildings_shouldReturnBuildingList() {
        // given
        var building1 = createBuilding(ID_1);
        var building2 = createBuilding(ID_2);
        when(buildingRepository.findAllByUniversityId(UNIVERSITY_ID)).thenReturn(Flux.just(building1, building2));

        // when
        Mono<List<BuildingResponseDto>> result = buildingService.listBuildings(UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(buildings -> assertEquals(2, buildings.size()))
                    .verifyComplete();
    }

    @Test
    void createBuilding_shouldCreateBuilding() {
        // given
        var buildingRequestDto = new BuildingRequestDto();
        when(buildingRepository.save(any(BuildingEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, BuildingEntity.class).id(ID_1)));

        // when
        Mono<BuildingResponseDto> result = buildingService.createBuilding(buildingRequestDto, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(building -> {
                        assertEquals(ID_1, building.id());
                        assertEquals(UNIVERSITY_ID, building.universityId());
                    })
                    .verifyComplete();
    }

    @Test
    void getBuildingById_shouldReturnBuildingWhenFound() {
        // given
        var building = createBuilding(ID_1);
        when(buildingRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.just(building));

        // when
        Mono<BuildingResponseDto> result = buildingService.getBuildingById(ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(buildingResponseDto -> {
                        assertEquals(ID_1, buildingResponseDto.id());
                        assertEquals(UNIVERSITY_ID, buildingResponseDto.universityId());
                    })
                    .verifyComplete();
    }

    @Test
    void getBuildingById_shouldThrowExceptionWhenBuildingNotFound() {
        // given
        when(buildingRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.empty());

        // when
        Mono<BuildingResponseDto> result = buildingService.getBuildingById(ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void updateBuilding_shouldUpdateBuildingWhenFound() {
        // given
        var building = createBuilding(ID_1);
        var buildingRequestDto = new BuildingRequestDto();
        when(buildingRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.just(building));
        when(buildingRepository.save(any(BuildingEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, BuildingEntity.class).id(ID_1)));

        // when
        Mono<BuildingResponseDto> result = buildingService.updateBuilding(buildingRequestDto, ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(buildingResponseDto -> {
                        assertEquals(ID_1, buildingResponseDto.id());
                        assertEquals(UNIVERSITY_ID, buildingResponseDto.universityId());
                    })
                    .verifyComplete();
    }

    @Test
    void updateBuilding_shouldThrowExceptionWhenBuildingNotFound() {
        // given
        var buildingRequestDto = new BuildingRequestDto();
        when(buildingRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.empty());

        // when
        Mono<BuildingResponseDto> result = buildingService.updateBuilding(buildingRequestDto, ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteBuilding_shouldDeleteBuildingWhenFound() {
        // given
        var building = createBuilding(ID_1);
        when(buildingRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.just(building));
        when(buildingRepository.deleteById(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<Boolean> result = buildingService.deleteBuilding(ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertTrue)
                    .verifyComplete();
    }

    @Test
    void deleteBuilding_shouldThrowExceptionWhenBuildingNotFound() {
        // given
        when(buildingRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.empty());

        // when
        Mono<Boolean> result = buildingService.deleteBuilding(ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteAllBuildingsByUniversityId() {
        // when
        when(buildingRepository.deleteAllByUniversityId(UNIVERSITY_ID)).thenReturn(Mono.empty());

        // given
        Mono<Void> result = buildingService.deleteAllBuildingsByUniversityId(UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    private BuildingEntity createBuilding(int id) {
        return BuildingEntity.builder()
                             .id(id)
                             .name("name")
                             .abbrev("abbrev")
                             .latitude(10.0)
                             .longitude(10.0)
                             .universityId(UNIVERSITY_ID)
                             .build();
    }
}