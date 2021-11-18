package pl.put.erasmusbackend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.put.erasmusbackend.database.model.BuildingEntity;
import pl.put.erasmusbackend.database.repository.BuildingRepository;
import pl.put.erasmusbackend.dto.BuildingRequestDto;
import pl.put.erasmusbackend.dto.BuildingResponseDto;
import pl.put.erasmusbackend.mapper.BuildingEntityToResponseDtoMapper;
import pl.put.erasmusbackend.mapper.BuildingRequestDtoToEntityMapper;
import pl.put.erasmusbackend.service.exception.NoSuchBuildingException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@Service
@Validated
@AllArgsConstructor
public class BuildingService {

    private final BuildingRepository buildingRepository;

    public Mono<List<BuildingEntity>> listBuildingByUniversityId(int universityId) {
        return buildingRepository.findByUniversityId(universityId)
                                 .collectList();
    }

    public Mono<BuildingResponseDto> createBuilding(int universityId, @Valid BuildingRequestDto buildingCreateDto) {
        return Mono.just(buildingCreateDto)
                   .map(BuildingRequestDtoToEntityMapper::from)
                   .map(building -> building.universityId(universityId))
                   .flatMap(buildingRepository::save)
                   .map(BuildingEntityToResponseDtoMapper::from);
    }

    public Mono<BuildingResponseDto> updateBuilding(int universityId, int buildingId, @Valid BuildingRequestDto buildingResponseDto) {
        return Mono.just(buildingResponseDto)
                   .map(BuildingRequestDtoToEntityMapper::from)
                   .map(building -> building.id(buildingId)
                                            .universityId(universityId))
                   .flatMap(updatedBuilding -> buildingRepository.findById(updatedBuilding.id())
                                                                 .switchIfEmpty(Mono.error(new NoSuchBuildingException()))
                                                                 .flatMap(b -> buildingRepository.save(updatedBuilding)))
                   .map(BuildingEntityToResponseDtoMapper::from);
    }

    public Mono<Boolean> deleteBuilding(int buildingId) {
        return buildingRepository.findById(buildingId)
                                 .switchIfEmpty(Mono.error(new NoSuchBuildingException()))
                                 .flatMap(b -> buildingRepository.deleteById(buildingId))
                                 .thenReturn(true);
    }
}
