package com.erapulus.server.mapper;

import com.erapulus.server.database.model.StudentEntity;
import com.erapulus.server.dto.StudentDto;
import org.springframework.stereotype.Component;

@Component
public class StudentEntityToDtoMapper implements EntityToResponseDtoMapper<StudentEntity, StudentDto> {

    public StudentDto from(StudentEntity studentEntity) {
        return StudentDto.builder()
                         .id(studentEntity.id())
                         .firstName(studentEntity.firstName())
                         .lastName(studentEntity.lastName())
                         .email(studentEntity.email())
                         .universityId(studentEntity.universityId())
                         .facebookUrl(studentEntity.facebookUrl())
                         .whatsUpUrl(studentEntity.whatsUpUrl())
                         .instagramUsername(studentEntity.instagramUsername())
                         .build();
    }
}
