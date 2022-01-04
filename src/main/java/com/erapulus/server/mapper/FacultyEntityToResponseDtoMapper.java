package com.erapulus.server.mapper;

import com.erapulus.server.database.model.FacultyEntity;
import com.erapulus.server.dto.faculty.FacultyResponseDto;
import org.springframework.stereotype.Component;

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
