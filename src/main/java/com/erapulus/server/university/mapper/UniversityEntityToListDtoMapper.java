package com.erapulus.server.university.mapper;

import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.university.database.UniversityEntity;
import com.erapulus.server.university.dto.UniversityListDto;
import org.springframework.stereotype.Component;

@Component
public class UniversityEntityToListDtoMapper implements EntityToResponseDtoMapper<UniversityEntity, UniversityListDto> {

    public UniversityListDto from(UniversityEntity universityEntity) {
        return UniversityListDto.builder()
                                .id(universityEntity.id())
                                .name(universityEntity.name())
                                .logoUrl(universityEntity.logoUrl())
                                .build();
    }
}
