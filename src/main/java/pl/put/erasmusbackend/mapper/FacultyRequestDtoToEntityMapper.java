package pl.put.erasmusbackend.mapper;

import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.database.model.FacultyEntity;
import pl.put.erasmusbackend.dto.FacultyRequestDto;

@Component
public class FacultyRequestDtoToEntityMapper implements RequestDtoToEntityMapper<FacultyRequestDto, FacultyEntity> {
    @Override
    public FacultyEntity from(FacultyRequestDto facultyRequestDto) {
        return FacultyEntity.builder()
                            .name(facultyRequestDto.name())
                            .email(facultyRequestDto.email())
                            .address(facultyRequestDto.address())
                            .build();
    }
}
