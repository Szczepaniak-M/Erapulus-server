package com.erapulus.server.mapper;

import com.erapulus.server.dto.building.BuildingRequestDto;
import org.springframework.stereotype.Component;
import com.erapulus.server.database.model.BuildingEntity;

@Component
public class BuildingRequestDtoToEntityMapper implements RequestDtoToEntityMapper<BuildingRequestDto, BuildingEntity> {

    public BuildingEntity from(BuildingRequestDto buildingRequestDto) {
        return BuildingEntity.builder()
                             .name(buildingRequestDto.name())
                             .abbrev(buildingRequestDto.abbrev())
                             .latitude(buildingRequestDto.latitude())
                             .longitude(buildingRequestDto.longitude())
                             .build();
    }
}
