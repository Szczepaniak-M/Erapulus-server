package com.erapulus.server.mapper.student;

import com.erapulus.server.database.model.StudentEntity;
import com.erapulus.server.dto.student.StudentListDto;
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
