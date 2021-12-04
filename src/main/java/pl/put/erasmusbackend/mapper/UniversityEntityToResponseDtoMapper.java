package pl.put.erasmusbackend.mapper;

import org.springframework.context.annotation.Configuration;
import pl.put.erasmusbackend.database.model.UniversityEntity;
import pl.put.erasmusbackend.dto.UniversityResponseDto;

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
                                    .primaryColor(universityEntity.primaryColor())
                                    .secondaryColor(universityEntity.secondaryColor())
                                    .build();
    }
}
