package pl.put.erasmusbackend.mapper;

import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.database.model.BuildingEntity;
import pl.put.erasmusbackend.dto.BuildingRequestDto;

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
