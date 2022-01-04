package com.erapulus.server.mapper.university;

import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import org.springframework.stereotype.Component;
import com.erapulus.server.database.model.UniversityEntity;
import com.erapulus.server.dto.university.UniversityRequestDto;

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
                               .primaryColor(universityRequestDto.primaryColor())
                               .secondaryColor(universityRequestDto.secondaryColor())
                               .build();
    }
}
