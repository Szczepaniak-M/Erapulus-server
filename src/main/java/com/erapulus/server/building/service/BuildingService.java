package com.erapulus.server.building.service;

import com.erapulus.server.building.database.BuildingEntity;
import com.erapulus.server.building.database.BuildingRepository;
import com.erapulus.server.building.dto.BuildingRequestDto;
import com.erapulus.server.building.dto.BuildingResponseDto;
import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.common.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.common.service.CrudGenericService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@Service
@Validated
public class BuildingService extends CrudGenericService<BuildingEntity, BuildingRequestDto, BuildingResponseDto> {

    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository,
                           RequestDtoToEntityMapper<BuildingRequestDto, BuildingEntity> requestDtoToEntityMapper,
                           EntityToResponseDtoMapper<BuildingEntity, BuildingResponseDto> entityToResponseDtoMapper) {
        super(buildingRepository, requestDtoToEntityMapper, entityToResponseDtoMapper, "building");
        this.buildingRepository = buildingRepository;
    }

    public Mono<List<BuildingResponseDto>> listBuildings(int universityId) {
        return buildingRepository.findAllByUniversityId(universityId)
                                 .map(entityToResponseDtoMapper::from)
                                 .collectList();
    }

    public Mono<BuildingResponseDto> createBuilding(@Valid BuildingRequestDto requestDto, int universityId) {
        UnaryOperator<BuildingEntity> addParamFromPath = building -> building.universityId(universityId);
        return createEntity(requestDto, addParamFromPath);
    }

    public Mono<BuildingResponseDto> getBuildingById(Integer buildingId, Integer universityId) {
        Supplier<Mono<BuildingEntity>> supplier = () -> buildingRepository.findByIdAndUniversityId(buildingId, universityId);
        return getEntityById(supplier);
    }

    public Mono<BuildingResponseDto> updateBuilding(@Valid BuildingRequestDto requestDto, int buildingId, int universityId) {
        UnaryOperator<BuildingEntity> addParamFromPath = building -> building.id(buildingId).universityId(universityId);
        Supplier<Mono<BuildingEntity>> supplier = () -> buildingRepository.findByIdAndUniversityId(buildingId, universityId);
        return updateEntity(requestDto, addParamFromPath, supplier);
    }

    public Mono<Boolean> deleteBuilding(int buildingId, int universityId) {
        Supplier<Mono<BuildingEntity>> supplier = () -> buildingRepository.findByIdAndUniversityId(buildingId, universityId);
        return deleteEntity(supplier);
    }

    public Mono<Void> deleteAllBuildingsByUniversityId(int universityId) {
        return buildingRepository.deleteAllByUniversityId(universityId);
    }
}
