package com.erapulus.server.service;

import com.erapulus.server.database.model.StudentEntity;
import com.erapulus.server.database.model.UserType;
import com.erapulus.server.database.repository.StudentRepository;
import com.erapulus.server.database.repository.UniversityRepository;
import com.erapulus.server.dto.*;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.mapper.StudentEntityToListDtoMapper;
import com.erapulus.server.mapper.UniversityEntityToResponseDtoMapper;
import com.erapulus.server.web.common.PageablePayload;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.NoSuchElementException;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.erapulus.server.service.QueryParamParser.parseString;

@Service
@Validated
public class StudentService extends CrudGenericService<StudentEntity, StudentRequestDto, StudentResponseDto> {

    private final StudentRepository studentRepository;
    private final UniversityRepository universityRepository;
    private final AzureStorageService azureStorageService;
    private final UniversityEntityToResponseDtoMapper universityEntityToResponseDtoMapper;

    public StudentService(StudentRepository studentRepository,
                          RequestDtoToEntityMapper<StudentRequestDto, StudentEntity> requestDtoToEntityMapper,
                          EntityToResponseDtoMapper<StudentEntity, StudentResponseDto> entityToResponseDtoMapper,
                          UniversityRepository universityRepository,
                          AzureStorageService azureStorageService,
                          UniversityEntityToResponseDtoMapper universityEntityToResponseDtoMapper) {
        super(studentRepository, requestDtoToEntityMapper, entityToResponseDtoMapper, "student");
        this.studentRepository = studentRepository;
        this.universityRepository = universityRepository;
        this.azureStorageService = azureStorageService;
        this.universityEntityToResponseDtoMapper = universityEntityToResponseDtoMapper;
    }

    public Mono<StudentResponseDto> getEntityById(int studentId) {
        Supplier<Mono<StudentEntity>> supplier = () -> studentRepository.findByIdAndType(studentId);
        return getEntityById(supplier);
    }

    public Mono<StudentResponseDto> updateEntity(@Valid StudentRequestDto requestDto, int studentId) {
        UnaryOperator<StudentEntity> addParamFromPath = student -> student.id(studentId).type(UserType.STUDENT);
        BinaryOperator<StudentEntity> mergeEntity = (oldStudent, newStudent) -> newStudent.pictureUrl(oldStudent.pictureUrl());
        return updateEntity(requestDto, addParamFromPath, mergeEntity);
    }

    public Mono<PageablePayload<StudentListDto>> listFriends(int studentId, String name, PageRequest pageRequest) {
        String nameParsed = parseString(name);
        return studentRepository.findByIdAndType(studentId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException(entityName)))
                                .thenMany(studentRepository.findFriendsByIdAndFilters(studentId, nameParsed, pageRequest.getOffset(), pageRequest.getPageSize()))
                                .map(StudentEntityToListDtoMapper::from)
                                .collectList()
                                .zipWith(studentRepository.countFriendsByIdAndFilters(studentId, nameParsed))
                                .map(dtoListAndTotalCount -> new PageablePayload<>(dtoListAndTotalCount.getT1(), pageRequest, dtoListAndTotalCount.getT2()));
    }

    public Mono<UniversityResponseDto> updateUniversity(@Valid StudentUniversityUpdateDto universityDto, int studentId) {
        return studentRepository.findByIdAndType(studentId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException(entityName)))
                                .flatMap(student -> universityRepository.findById(universityDto.universityId())
                                                                        .zipWith(Mono.just(student.universityId(universityDto.universityId()))))
                                .switchIfEmpty(Mono.error(new NoSuchElementException("university")))
                                .flatMap(universityAndStudent -> studentRepository.save(universityAndStudent.getT2())
                                                                                  .thenReturn(universityAndStudent.getT1()))
                                .map(universityEntityToResponseDtoMapper::from);

    }

    public Mono<StudentResponseDto> updatePhoto(int studentId, FilePart photo) {
        String filePath = "user/%d/photo/%s".formatted(studentId, photo.filename());
        return studentRepository.findByIdAndType(studentId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException(entityName)))
                                .flatMap(student -> azureStorageService.uploadFile(photo, filePath)
                                                                       .flatMap(path -> studentRepository.save(student.pictureUrl(path))))
                                .map(entityToResponseDtoMapper::from);
    }
}
