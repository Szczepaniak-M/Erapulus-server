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
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import static com.erapulus.server.service.QueryParamParser.parseString;

@Service
@Validated
public class StudentService extends CrudGenericService<StudentEntity, StudentRequestDto, StudentResponseDto> {

    private final StudentRepository studentRepository;
    private final UniversityRepository universityRepository;
    private final AzureStorageService azureStorageService;
    private static final String URL_COMMON_PART = "https://erapulus.blob.core.windows.net/erapulus";

    public StudentService(StudentRepository studentRepository,
                          RequestDtoToEntityMapper<StudentRequestDto, StudentEntity> requestDtoToEntityMapper,
                          EntityToResponseDtoMapper<StudentEntity, StudentResponseDto> entityToResponseDtoMapper,
                          UniversityRepository universityRepository,
                          AzureStorageService azureStorageService) {
        super(studentRepository, requestDtoToEntityMapper, entityToResponseDtoMapper);
        this.studentRepository = studentRepository;
        this.universityRepository = universityRepository;
        this.azureStorageService = azureStorageService;
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

    public Mono<Object> updatePhoto(int studentId, FilePart photo) {
        String filePath = "user/%d/photo/%s".formatted(studentId, photo.filename());
        return studentRepository.findByIdAndType(studentId)
                                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                                .flatMap(student -> azureStorageService.uploadFile(photo, filePath)
                                                                       .then(studentRepository.save(student.pictureUrl(URL_COMMON_PART + filePath))))
                                .map(entityToResponseDtoMapper::from);
    }
}
