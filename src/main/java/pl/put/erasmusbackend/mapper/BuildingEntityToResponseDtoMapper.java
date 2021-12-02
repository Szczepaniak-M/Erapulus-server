package pl.put.erasmusbackend.mapper;

import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.database.model.BuildingEntity;
import pl.put.erasmusbackend.dto.BuildingResponseDto;

@Component
public class BuildingEntityToResponseDtoMapper implements EntityToResponseDtoMapper<BuildingEntity, BuildingResponseDto> {

    public BuildingResponseDto from(BuildingEntity buildingEntity) {
        return BuildingResponseDto.builder()
                                  .id(buildingEntity.id())
                                  .name(buildingEntity.name())
                                  .abbrev(buildingEntity.abbrev())
                                  .latitude(buildingEntity.latitude())
                                  .longitude(buildingEntity.longitude())
                                  .universityId(buildingEntity.universityId())
                                  .build();
    }
}
