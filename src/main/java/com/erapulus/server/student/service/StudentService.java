package com.erapulus.server.student.service;

import com.erapulus.server.common.database.UserType;
import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.common.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.common.service.AzureStorageService;
import com.erapulus.server.common.service.CrudGenericService;
import com.erapulus.server.student.database.StudentEntity;
import com.erapulus.server.student.database.StudentRepository;
import com.erapulus.server.student.dto.StudentListDto;
import com.erapulus.server.student.dto.StudentRequestDto;
import com.erapulus.server.student.dto.StudentResponseDto;
import com.erapulus.server.student.dto.StudentUniversityUpdateDto;
import com.erapulus.server.student.mapper.StudentEntityToListDtoMapper;
import com.erapulus.server.university.database.UniversityRepository;
import com.erapulus.server.university.dto.UniversityResponseDto;
import com.erapulus.server.university.mapper.UniversityEntityToResponseDtoMapper;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.erapulus.server.common.service.QueryParamParser.parseString;
import static com.erapulus.server.security.SecurityContextUtils.withSecurityContext;

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

    public Mono<List<StudentListDto>> listStudents(String name) {
        String parsedName = parseString(name);
        if (parsedName == null) {
            return Mono.just(Collections.emptyList());
        }
        return withSecurityContext(user -> studentRepository.findAllByNameAndUniversityId(user.id(), parsedName, user.universityId())
                                                            .map(StudentEntityToListDtoMapper::from)
                                                            .collectList());
    }

    public Mono<StudentResponseDto> getStudentById(int studentId) {
        Supplier<Mono<StudentEntity>> supplier = () -> studentRepository.findByIdAndType(studentId);
        return getEntityById(supplier);
    }

    public Mono<StudentResponseDto> updateStudent(@Valid StudentRequestDto requestDto, int studentId) {
        UnaryOperator<StudentEntity> addParamFromPath = student -> student.id(studentId).type(UserType.STUDENT);
        Supplier<Mono<StudentEntity>> supplier = () -> studentRepository.findByIdAndType(studentId);
        BinaryOperator<StudentEntity> mergeEntity = (oldStudent, newStudent) -> newStudent.pictureUrl(oldStudent.pictureUrl())
                                                                                          .universityId(oldStudent.universityId())
                                                                                          .email(oldStudent.email());
        return updateEntity(requestDto, addParamFromPath, supplier, mergeEntity);
    }

    public Mono<UniversityResponseDto> updateStudentUniversity(@Valid StudentUniversityUpdateDto universityDto, int studentId) {
        return studentRepository.findByIdAndType(studentId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException(entityName)))
                                .flatMap(student -> universityRepository.findById(universityDto.universityId())
                                                                        .zipWith(Mono.just(student.universityId(universityDto.universityId()))))
                                .switchIfEmpty(Mono.error(new NoSuchElementException("university")))
                                .flatMap(universityAndStudent -> studentRepository.save(universityAndStudent.getT2())
                                                                                  .thenReturn(universityAndStudent.getT1()))
                                .map(universityEntityToResponseDtoMapper::from);

    }

    @Transactional
    public Mono<StudentResponseDto> updateStudentPhoto(int studentId, FilePart photo) {
        String filePath = "user/%d/photo/%s".formatted(studentId, photo.filename());
        return studentRepository.findByIdAndType(studentId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException(entityName)))
                                .flatMap(student -> azureStorageService.uploadFile(photo, filePath)
                                                                       .flatMap(path -> studentRepository.save(student.pictureUrl(path))))
                                .map(entityToResponseDtoMapper::from);
    }
}
