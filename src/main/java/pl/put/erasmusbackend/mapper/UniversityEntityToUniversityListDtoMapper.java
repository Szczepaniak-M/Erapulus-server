package pl.put.erasmusbackend.mapper;

import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.database.model.UniversityEntity;
import pl.put.erasmusbackend.dto.UniversityListDto;

@Component
public class UniversityEntityToUniversityListDtoMapper implements EntityToResponseDtoMapper<UniversityEntity, UniversityListDto> {

    public UniversityListDto from(UniversityEntity universityEntity) {
        return UniversityListDto.builder()
                                .id(universityEntity.id())
                                .name(universityEntity.name())
                                .build();
    }
}
