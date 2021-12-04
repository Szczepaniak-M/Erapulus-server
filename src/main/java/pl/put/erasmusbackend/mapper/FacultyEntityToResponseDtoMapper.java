package pl.put.erasmusbackend.mapper;

import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.database.model.FacultyEntity;
import pl.put.erasmusbackend.dto.FacultyResponseDto;

@Component
public class FacultyEntityToResponseDtoMapper implements EntityToResponseDtoMapper<FacultyEntity, FacultyResponseDto> {
    @Override
    public FacultyResponseDto from(FacultyEntity facultyEntity) {
        return FacultyResponseDto.builder()
                                 .id(facultyEntity.id())
                                 .name(facultyEntity.name())
                                 .email(facultyEntity.email())
                                 .address(facultyEntity.address())
                                 .universityId(facultyEntity.universityId())
                                 .build();
    }
}
