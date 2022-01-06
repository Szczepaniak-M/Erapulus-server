package com.erapulus.server.university.mapper;

import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.university.database.UniversityEntity;
import com.erapulus.server.university.dto.UniversityResponseDto;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UniversityEntityToResponseDtoMapper implements EntityToResponseDtoMapper<UniversityEntity, UniversityResponseDto> {
    public UniversityResponseDto from(UniversityEntity universityEntity) {
        return UniversityResponseDto.builder()
                                    .id(universityEntity.id())
                                    .name(universityEntity.name())
                                    .address(universityEntity.address())
                                    .address2(universityEntity.address2())
                                    .city(universityEntity.city())
                                    .zipcode(universityEntity.zipcode())
                                    .country(universityEntity.country())
                                    .description(universityEntity.description())
                                    .websiteUrl(universityEntity.websiteUrl())
                                    .logoUrl(universityEntity.logoUrl())
                                    .build();
    }
}
