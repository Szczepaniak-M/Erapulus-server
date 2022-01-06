package com.erapulus.server.faculty.mapper;

import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.faculty.database.FacultyEntity;
import com.erapulus.server.faculty.dto.FacultyResponseDto;
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
