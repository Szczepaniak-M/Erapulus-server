package com.erapulus.server.mapper.university;

import com.erapulus.server.database.model.UniversityEntity;
import com.erapulus.server.dto.university.UniversityListDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class UniversityEntityToUniversityListDtoMapper implements EntityToResponseDtoMapper<UniversityEntity, UniversityListDto> {

    public UniversityListDto from(UniversityEntity universityEntity) {
        return UniversityListDto.builder()
                                .id(universityEntity.id())
                                .name(universityEntity.name())
                                .logoUrl(universityEntity.logoUrl())
                                .build();
    }
}
