package com.erapulus.server.mapper.student;

import com.erapulus.server.database.model.StudentEntity;
import com.erapulus.server.dto.student.StudentResponseDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class StudentEntityToDtoMapper implements EntityToResponseDtoMapper<StudentEntity, StudentResponseDto> {

    public StudentResponseDto from(StudentEntity studentEntity) {
        return StudentResponseDto.builder()
                                 .id(studentEntity.id())
                                 .firstName(studentEntity.firstName())
                                 .lastName(studentEntity.lastName())
                                 .email(studentEntity.email())
                                 .universityId(studentEntity.universityId())
                                 .phoneNumber(studentEntity.phoneNumber())
                                 .pictureUrl(studentEntity.pictureUrl())
                                 .facebookUrl(studentEntity.facebookUrl())
                                 .whatsUpUrl(studentEntity.whatsUpUrl())
                                 .instagramUsername(studentEntity.instagramUsername())
                                 .build();
    }
}
