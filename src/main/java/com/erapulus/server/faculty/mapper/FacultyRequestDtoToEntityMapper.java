package com.erapulus.server.faculty.mapper;

import com.erapulus.server.common.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.faculty.database.FacultyEntity;
import com.erapulus.server.faculty.dto.FacultyRequestDto;
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
