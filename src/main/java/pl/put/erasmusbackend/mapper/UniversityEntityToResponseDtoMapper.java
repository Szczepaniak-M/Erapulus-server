package pl.put.erasmusbackend.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.put.erasmusbackend.database.model.UniversityEntity;
import pl.put.erasmusbackend.dto.UniversityResponseDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UniversityEntityToResponseDtoMapper {
    public static UniversityResponseDto from(UniversityEntity universityEntity) {
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
