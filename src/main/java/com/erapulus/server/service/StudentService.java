package com.erapulus.server.service;

import com.erapulus.server.database.repository.StudentRepository;
import com.erapulus.server.dto.StudentDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.erapulus.server.mapper.StudentEntityToDtoMapper;
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
