package com.erapulus.server.university.mapper;

import com.erapulus.server.common.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.university.database.UniversityEntity;
import com.erapulus.server.university.dto.UniversityRequestDto;
import org.springframework.stereotype.Component;

@Component
public class UniversityRequestDtoToEntityMapper implements RequestDtoToEntityMapper<UniversityRequestDto, UniversityEntity> {
    public UniversityEntity from(UniversityRequestDto universityRequestDto) {
        return UniversityEntity.builder()
                               .name(universityRequestDto.name())
                               .address(universityRequestDto.address())
                               .address2(universityRequestDto.address2())
                               .city(universityRequestDto.city())
                               .zipcode(universityRequestDto.zipcode())
                               .country(universityRequestDto.country())
                               .description(universityRequestDto.description())
                               .websiteUrl(universityRequestDto.websiteUrl())
                               .build();
    }
}
