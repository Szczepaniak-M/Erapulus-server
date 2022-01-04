package com.erapulus.server.mapper;

import com.erapulus.server.database.model.FacultyEntity;
import com.erapulus.server.dto.faculty.FacultyRequestDto;
import org.springframework.stereotype.Component;

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
