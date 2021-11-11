package pl.put.erasmusbackend.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.put.erasmusbackend.database.model.StudentEntity;
import pl.put.erasmusbackend.dto.StudentDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudentEntityToDtoMapper {

    public static StudentDto from(StudentEntity studentEntity) {
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
