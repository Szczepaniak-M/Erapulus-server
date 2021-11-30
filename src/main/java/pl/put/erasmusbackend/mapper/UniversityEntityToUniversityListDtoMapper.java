package pl.put.erasmusbackend.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.put.erasmusbackend.database.model.UniversityEntity;
import pl.put.erasmusbackend.dto.UniversityListDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UniversityEntityToUniversityListDtoMapper {

    public static UniversityListDto from(UniversityEntity universityEntity) {
        return UniversityListDto.builder()
                                .id(universityEntity.id())
                                .name(universityEntity.name())
                                .build();
    }
}
