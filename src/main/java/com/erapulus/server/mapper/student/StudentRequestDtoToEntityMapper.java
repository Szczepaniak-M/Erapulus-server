package com.erapulus.server.mapper.student;

import com.erapulus.server.database.model.StudentEntity;
import com.erapulus.server.dto.student.StudentRequestDto;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class StudentRequestDtoToEntityMapper implements RequestDtoToEntityMapper<StudentRequestDto, StudentEntity> {
    @Override
    public StudentEntity from(StudentRequestDto studentRequestDto) {
        return StudentEntity.builder()
                            .firstName(studentRequestDto.firstName())
                            .lastName(studentRequestDto.lastName())
                            .email(studentRequestDto.email())
                            .universityId(studentRequestDto.universityId())
                            .phoneNumber(studentRequestDto.phoneNumber())
                            .facebookUrl(studentRequestDto.facebookUrl())
                            .whatsUpUrl(studentRequestDto.whatsUpUrl())
                            .instagramUsername(studentRequestDto.instagramUsername())
                            .build();
    }
}
