package com.erapulus.server.building.mapper;

import com.erapulus.server.building.database.BuildingEntity;
import com.erapulus.server.building.dto.BuildingResponseDto;
import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import org.springframework.stereotype.Component;

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
