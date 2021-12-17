package com.erapulus.server.service;

import com.erapulus.server.database.model.StudentEntity;
import com.erapulus.server.database.repository.StudentRepository;
import com.erapulus.server.dto.StudentRequestDto;
import com.erapulus.server.dto.StudentResponseDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@Service
@Validated
public class StudentService extends CrudGenericService<StudentEntity, StudentRequestDto, StudentResponseDto> {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository,
                          RequestDtoToEntityMapper<StudentRequestDto, StudentEntity> requestDtoToEntityMapper,
                          EntityToResponseDtoMapper<StudentEntity, StudentResponseDto> entityToResponseDtoMapper) {
        super(studentRepository, requestDtoToEntityMapper, entityToResponseDtoMapper);
        this.studentRepository = studentRepository;
    }

    public Mono<StudentResponseDto> getEntityById(int studentId) {
        Supplier<Mono<StudentEntity>> supplier = () -> studentRepository.findById(studentId);
        return getEntityById(supplier);
    }

    public Mono<StudentResponseDto> updateEntity(@Valid StudentRequestDto requestDto, int studentId) {
        UnaryOperator<StudentEntity> addParamFromPath = student -> student.id(studentId);
        return updateEntity(requestDto, addParamFromPath);
    }

    @Override
    @Transactional
    public Mono<Boolean> deleteEntity(int studentId) {
        // TODO Add deleting Friendship and Devices
        return studentRepository.findById(studentId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException()))
                                .flatMap(b -> studentRepository.deleteById(studentId))
                                .thenReturn(true);
    }
}
