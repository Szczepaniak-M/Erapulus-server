package com.erapulus.server.service;

import com.erapulus.server.database.model.StudentEntity;
import com.erapulus.server.database.model.UserType;
import com.erapulus.server.database.repository.StudentRepository;
import com.erapulus.server.database.repository.UniversityRepository;
import com.erapulus.server.dto.StudentListDto;
import com.erapulus.server.dto.StudentRequestDto;
import com.erapulus.server.dto.StudentResponseDto;
import com.erapulus.server.dto.StudentUniversityUpdateDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.mapper.StudentEntityToListDtoMapper;
import com.erapulus.server.service.exception.NoSuchParentElementException;
import com.erapulus.server.web.common.PageablePayload;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import static com.erapulus.server.service.QueryParamParser.*;

@Service
@Validated
public class StudentService extends CrudGenericService<StudentEntity, StudentRequestDto, StudentResponseDto> {

    private final StudentRepository studentRepository;
    private final UniversityRepository universityRepository;

    public StudentService(StudentRepository studentRepository,
                          RequestDtoToEntityMapper<StudentRequestDto, StudentEntity> requestDtoToEntityMapper,
                          EntityToResponseDtoMapper<StudentEntity, StudentResponseDto> entityToResponseDtoMapper,
                          UniversityRepository universityRepository) {
        super(studentRepository, requestDtoToEntityMapper, entityToResponseDtoMapper);
        this.studentRepository = studentRepository;
        this.universityRepository = universityRepository;
    }

    public Mono<StudentResponseDto> getEntityById(int studentId) {
        Supplier<Mono<StudentEntity>> supplier = () -> studentRepository.findByIdAndType(studentId);
        return getEntityById(supplier);
    }

    public Mono<StudentResponseDto> updateEntity(@Valid StudentRequestDto requestDto, int studentId) {
        return Mono.just(requestDto)
                   .map(requestDtoToEntityMapper::from)
                   .map(employee -> employee.id(studentId).type(UserType.STUDENT))
                   .flatMap(updatedT -> studentRepository.findById(updatedT.id())
                                                         .switchIfEmpty(Mono.error(new NoSuchElementException()))
                                                         .flatMap(oldEntity -> studentRepository.save(updatedT.pictureUrl(oldEntity.pictureUrl()))))
                   .map(entityToResponseDtoMapper::from);
    }

    @Override
    @Transactional
    public Mono<Boolean> deleteEntity(int studentId) {
        // TODO Add deleting Friendship and Devices
        return studentRepository.findByIdAndType(studentId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException()))
                                .flatMap(b -> studentRepository.deleteById(studentId))
                                .thenReturn(true);
    }

    public Mono<PageablePayload<StudentListDto>> listFriends(int studentId, String name, PageRequest pageRequest) {
        String nameParsed = parseString(name);
        return studentRepository.findByIdAndType(studentId)
                                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                                .thenMany(studentRepository.findFriendsByIdAndFilters(studentId, nameParsed, pageRequest.getOffset(), pageRequest.getPageSize()))
                                .map(StudentEntityToListDtoMapper::from)
                                .collectList()
                                .zipWith(studentRepository.countFriendsByIdAndFilters(studentId, nameParsed))
                                .map(dtoListAndTotalCount -> new PageablePayload<>(dtoListAndTotalCount.getT1(), pageRequest, dtoListAndTotalCount.getT2()));
    }

    public Mono<StudentUniversityUpdateDto> updateUniversity(@Validated StudentUniversityUpdateDto universityDto, int studentId) {
        return studentRepository.findByIdAndType(studentId)
                                .switchIfEmpty(Mono.error(NoSuchParentElementException::new))
                                .flatMap(student -> universityRepository.existsById(universityDto.universityId())
                                                                        .flatMap(exist -> exist ? Mono.just(student.universityId(universityDto.universityId()))
                                                                                                : Mono.error(NoSuchElementException::new)))
                                .thenReturn(universityDto);
    }
}
