package pl.put.erasmusbackend.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.put.erasmusbackend.database.model.BuildingEntity;
import pl.put.erasmusbackend.dto.BuildingResponseDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BuildingEntityToResponseDtoMapper {

    public static BuildingResponseDto from(BuildingEntity buildingEntity) {
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
