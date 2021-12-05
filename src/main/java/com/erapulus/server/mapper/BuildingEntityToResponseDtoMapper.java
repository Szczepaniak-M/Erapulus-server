package com.erapulus.server.mapper;

import org.springframework.stereotype.Component;
import com.erapulus.server.database.model.BuildingEntity;
import com.erapulus.server.dto.BuildingResponseDto;

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
