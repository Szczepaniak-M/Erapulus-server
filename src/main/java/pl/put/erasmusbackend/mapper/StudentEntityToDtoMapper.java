package pl.put.erasmusbackend.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.put.erasmusbackend.database.model.Student;
import pl.put.erasmusbackend.dto.StudentDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudentEntityToDtoMapper {

    public static StudentDto from(Student student) {
        return StudentDto.builder()
                         .id(student.id())
                         .firstName(student.firstName())
                         .lastName(student.lastName())
                         .email(student.email())
                         .universityId(student.universityId())
                         .facebookUrl(student.facebookUrl())
                         .whatsUpUrl(student.whatsUpUrl())
                         .instagramUsername(student.instagramUsername())
                         .build();
    }
}
