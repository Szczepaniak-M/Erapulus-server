package pl.put.erasmusbackend.mapper;

import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.database.model.StudentEntity;
import pl.put.erasmusbackend.dto.StudentDto;

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
