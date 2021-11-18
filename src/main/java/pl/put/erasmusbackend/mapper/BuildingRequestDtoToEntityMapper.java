package pl.put.erasmusbackend.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.put.erasmusbackend.database.model.BuildingEntity;
import pl.put.erasmusbackend.dto.BuildingRequestDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BuildingRequestDtoToEntityMapper {

    public static BuildingEntity from(BuildingRequestDto buildingRequestDto) {
        return BuildingEntity.builder()
                             .name(buildingRequestDto.name())
                             .abbrev(buildingRequestDto.abbrev())
                             .latitude(buildingRequestDto.latitude())
                             .longitude(buildingRequestDto.longitude())
                             .build();
    }
}
