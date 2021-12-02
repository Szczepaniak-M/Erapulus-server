package pl.put.erasmusbackend.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.put.erasmusbackend.database.model.BuildingEntity;
import pl.put.erasmusbackend.database.repository.BuildingRepository;
import pl.put.erasmusbackend.dto.BuildingRequestDto;
import pl.put.erasmusbackend.dto.BuildingResponseDto;
import pl.put.erasmusbackend.mapper.EntityToResponseDtoMapper;
import pl.put.erasmusbackend.mapper.RequestDtoToEntityMapper;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.UnaryOperator;

@Service
@Validated
public class BuildingService extends CrudGenericService<BuildingEntity, BuildingRequestDto, BuildingResponseDto> {

    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository,
                           RequestDtoToEntityMapper<BuildingRequestDto, BuildingEntity> requestDtoToEntityMapper,
                           EntityToResponseDtoMapper<BuildingEntity, BuildingResponseDto> entityToResponseDtoMapper) {
        super(buildingRepository, requestDtoToEntityMapper, entityToResponseDtoMapper);
        this.buildingRepository = buildingRepository;
    }

    public Mono<List<BuildingEntity>> listBuildingByUniversityId(int universityId) {
        return buildingRepository.findByUniversityId(universityId)
                                 .collectList();
    }

    public Mono<BuildingResponseDto> createEntity(BuildingRequestDto requestDto, int universityId) {
        UnaryOperator<BuildingEntity> addParamFromPath = building -> building.universityId(universityId);
        return createEntity(requestDto, addParamFromPath);
    }

    public Mono<BuildingResponseDto> updateEntity(BuildingRequestDto requestDto, int buildingId, int universityId) {
        UnaryOperator<BuildingEntity> addParamFromPath = building -> building.id(buildingId).universityId(universityId);
        return updateEntity(requestDto, addParamFromPath);
    }
}
