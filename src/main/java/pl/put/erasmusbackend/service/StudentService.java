package pl.put.erasmusbackend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.put.erasmusbackend.database.repository.StudentRepository;
import pl.put.erasmusbackend.dto.StudentDto;
import pl.put.erasmusbackend.mapper.StudentEntityToDtoMapper;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentEntityToDtoMapper studentEntityToDtoMapper;

    public Mono<StudentDto> getStudent(int studentId) {
        return studentRepository.findById(studentId)
                                .map(studentEntityToDtoMapper::from);
    }
}
