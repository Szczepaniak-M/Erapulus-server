package pl.put.erasmusbackend.mapper;

import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.database.model.UniversityEntity;
import pl.put.erasmusbackend.dto.UniversityRequestDto;

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
                               .logoUrl(universityRequestDto.logoUrl())
                               .primaryColor(universityRequestDto.primaryColor())
                               .secondaryColor(universityRequestDto.secondaryColor())
                               .build();
    }
}
