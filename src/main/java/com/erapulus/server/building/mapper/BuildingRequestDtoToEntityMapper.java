package com.erapulus.server.building.mapper;

import com.erapulus.server.building.database.BuildingEntity;
import com.erapulus.server.building.dto.BuildingRequestDto;
import com.erapulus.server.common.mapper.RequestDtoToEntityMapper;
import org.springframework.stereotype.Component;

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
