package com.erapulus.server.student.mapper;

import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.student.database.StudentEntity;
import com.erapulus.server.student.dto.StudentResponseDto;
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
