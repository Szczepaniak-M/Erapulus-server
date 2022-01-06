package com.erapulus.server.student.mapper;

import com.erapulus.server.student.database.StudentEntity;
import com.erapulus.server.student.dto.StudentListDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudentEntityToListDtoMapper {

    public static StudentListDto from(StudentEntity studentEntity) {
        return StudentListDto.builder()
                             .id(studentEntity.id())
                             .firstName(studentEntity.firstName())
                             .lastName(studentEntity.lastName())
                             .pictureUrl(studentEntity.pictureUrl())
                             .build();
    }
}
