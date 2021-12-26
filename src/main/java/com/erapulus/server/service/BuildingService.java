package com.erapulus.server.service;

import com.erapulus.server.database.model.BuildingEntity;
import com.erapulus.server.database.repository.BuildingRepository;
import com.erapulus.server.database.repository.UniversityRepository;
import com.erapulus.server.dto.BuildingRequestDto;
import com.erapulus.server.dto.BuildingResponseDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.UnaryOperator;

@Service
@Validated
public class BuildingService extends CrudGenericService<BuildingEntity, BuildingRequestDto, BuildingResponseDto> {

    private final BuildingRepository buildingRepository;
    private final UniversityRepository universityRepository;

    public BuildingService(BuildingRepository buildingRepository,
                           RequestDtoToEntityMapper<BuildingRequestDto, BuildingEntity> requestDtoToEntityMapper,
                           EntityToResponseDtoMapper<BuildingEntity, BuildingResponseDto> entityToResponseDtoMapper,
                           UniversityRepository universityRepository) {
        super(buildingRepository, requestDtoToEntityMapper, entityToResponseDtoMapper, "building");
        this.buildingRepository = buildingRepository;
        this.universityRepository = universityRepository;
    }

    public Mono<List<BuildingResponseDto>> listEntities(int universityId) {
        return universityRepository.existsById(universityId)
                                   .switchIfEmpty(Mono.error(new NoSuchElementException("university")))
                                   .thenMany(buildingRepository.findAllByUniversityId(universityId))
                                   .map(entityToResponseDtoMapper::from)
                                   .collectList();
    }

    public Mono<BuildingResponseDto> createEntity(@Valid BuildingRequestDto requestDto, int universityId) {
        UnaryOperator<BuildingEntity> addParamFromPath = building -> building.universityId(universityId);
        return createEntity(requestDto, addParamFromPath);
    }

    public Mono<BuildingResponseDto> updateEntity(@Valid BuildingRequestDto requestDto, int buildingId, int universityId) {
        UnaryOperator<BuildingEntity> addParamFromPath = building -> building.id(buildingId).universityId(universityId);
        return updateEntity(requestDto, addParamFromPath);
    }
}
