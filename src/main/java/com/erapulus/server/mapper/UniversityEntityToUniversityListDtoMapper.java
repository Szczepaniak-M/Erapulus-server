package com.erapulus.server.mapper;

import com.erapulus.server.database.model.UniversityEntity;
import com.erapulus.server.dto.UniversityListDto;
import org.springframework.stereotype.Component;

@Component
public class UniversityEntityToUniversityListDtoMapper implements EntityToResponseDtoMapper<UniversityEntity, UniversityListDto> {

    public UniversityListDto from(UniversityEntity universityEntity) {
        return UniversityListDto.builder()
                                .id(universityEntity.id())
                                .name(universityEntity.name())
                                .build();
    }
}
