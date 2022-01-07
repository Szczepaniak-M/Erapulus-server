package com.erapulus.server.university.service;

import com.erapulus.server.building.service.BuildingService;
import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.common.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.common.service.AzureStorageService;
import com.erapulus.server.common.service.CrudGenericService;
import com.erapulus.server.document.service.DocumentService;
import com.erapulus.server.employee.service.EmployeeService;
import com.erapulus.server.faculty.service.FacultyService;
import com.erapulus.server.post.service.PostService;
import com.erapulus.server.university.database.UniversityEntity;
import com.erapulus.server.university.database.UniversityRepository;
import com.erapulus.server.university.dto.UniversityListDto;
import com.erapulus.server.university.dto.UniversityRequestDto;
import com.erapulus.server.university.dto.UniversityResponseDto;
import com.erapulus.server.university.mapper.UniversityEntityToListDtoMapper;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@Service
@Validated
public class UniversityService extends CrudGenericService<UniversityEntity, UniversityRequestDto, UniversityResponseDto> {

    private final UniversityRepository universityRepository;
    private final UniversityEntityToListDtoMapper universityEntityToListDtoMapper;
    private final AzureStorageService azureStorageService;
    private final FacultyService facultyService;
    private final DocumentService documentService;
    private final PostService postService;
    private final EmployeeService employeeService;
    private final BuildingService buildingService;


    public UniversityService(UniversityRepository universityRepository,
                             RequestDtoToEntityMapper<UniversityRequestDto, UniversityEntity> requestDtoToEntityMapper,
                             EntityToResponseDtoMapper<UniversityEntity, UniversityResponseDto> entityToResponseDtoMapper,
                             UniversityEntityToListDtoMapper universityEntityToListDtoMapper,
                             AzureStorageService azureStorageService,
                             FacultyService facultyService, DocumentService documentService,
                             PostService postService,
                             EmployeeService employeeService,
                             BuildingService buildingService) {
        super(universityRepository, requestDtoToEntityMapper, entityToResponseDtoMapper, "university");
        this.universityRepository = universityRepository;
        this.universityEntityToListDtoMapper = universityEntityToListDtoMapper;
        this.azureStorageService = azureStorageService;
        this.facultyService = facultyService;
        this.documentService = documentService;
        this.postService = postService;
        this.employeeService = employeeService;
        this.buildingService = buildingService;
    }

    public Mono<List<UniversityListDto>> listUniversities() {
        return universityRepository.findAllUniversities()
                                   .map(universityEntityToListDtoMapper::from)
                                   .collectList();
    }

    public Mono<UniversityResponseDto> createUniversity(@Valid UniversityRequestDto requestDto) {
        UnaryOperator<UniversityEntity> addParamFromPath = university -> university;
        return createEntity(requestDto, addParamFromPath);
    }

    public Mono<UniversityResponseDto> getUniversityById(int universityId) {
        Supplier<Mono<UniversityEntity>> supplier = () -> universityRepository.findById(universityId);
        return getEntityById(supplier);
    }

    public Mono<UniversityResponseDto> updateUniversity(@Valid UniversityRequestDto requestDto, int universityId) {
        UnaryOperator<UniversityEntity> addParamFromPath = university -> university.id(universityId);
        Supplier<Mono<UniversityEntity>> supplier = () -> universityRepository.findById(universityId);
        BinaryOperator<UniversityEntity> mergeEntity = (oldUniversity, newUniversity) -> newUniversity.logoUrl(oldUniversity.logoUrl());
        return updateEntity(requestDto, addParamFromPath, supplier, mergeEntity);
    }

    @Transactional
    public Mono<Boolean> deleteUniversity(int universityId) {
        Supplier<Mono<UniversityEntity>> supplier = () -> universityRepository.findById(universityId);
        return facultyService.deleteAllFacultiesByUniversityId(universityId)
                             .thenMany(documentService.deleteAllDocumentsByUniversityId(universityId))
                             .thenMany(buildingService.deleteAllBuildingsByUniversityId(universityId))
                             .thenMany(postService.deleteAllPostsByUniversityId(universityId))
                             .thenMany(employeeService.deleteAllEmployeesByUniversityId(universityId))
                             .then(deleteEntity(supplier));
    }

    public Mono<UniversityResponseDto> updateUniversityLogo(Integer universityId, FilePart photo) {
        String filePath = "university/%d/logo/%s".formatted(universityId, photo.filename());
        return universityRepository.findById(universityId)
                                   .switchIfEmpty(Mono.error(new NoSuchElementException(entityName)))
                                   .flatMap(student -> azureStorageService.uploadFile(photo, filePath)
                                                                          .flatMap(path -> universityRepository.save(student.logoUrl(path))))
                                   .map(entityToResponseDtoMapper::from);
    }
}
