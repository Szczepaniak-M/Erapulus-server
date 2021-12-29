package com.erapulus.server.service;

import com.erapulus.server.database.model.UniversityEntity;
import com.erapulus.server.database.repository.UniversityRepository;
import com.erapulus.server.dto.UniversityListDto;
import com.erapulus.server.dto.UniversityRequestDto;
import com.erapulus.server.dto.UniversityResponseDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.mapper.UniversityEntityToUniversityListDtoMapper;
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
    private final UniversityEntityToUniversityListDtoMapper universityEntityToUniversityListDtoMapper;
    private final AzureStorageService azureStorageService;
    private final FacultyService facultyService;
    private final DocumentService documentService;
    private final PostService postService;
    private final EmployeeService employeeService;
    private final BuildingService buildingService;


    public UniversityService(UniversityRepository universityRepository,
                             RequestDtoToEntityMapper<UniversityRequestDto, UniversityEntity> requestDtoToEntityMapper,
                             EntityToResponseDtoMapper<UniversityEntity, UniversityResponseDto> entityToResponseDtoMapper,
                             UniversityEntityToUniversityListDtoMapper universityEntityToUniversityListDtoMapper,
                             AzureStorageService azureStorageService,
                             FacultyService facultyService, DocumentService documentService,
                             PostService postService,
                             EmployeeService employeeService,
                             BuildingService buildingService) {
        super(universityRepository, requestDtoToEntityMapper, entityToResponseDtoMapper, "university");
        this.universityRepository = universityRepository;
        this.universityEntityToUniversityListDtoMapper = universityEntityToUniversityListDtoMapper;
        this.azureStorageService = azureStorageService;
        this.facultyService = facultyService;
        this.documentService = documentService;
        this.postService = postService;
        this.employeeService = employeeService;
        this.buildingService = buildingService;
    }

    public Mono<List<UniversityListDto>> listUniversities() {
        return universityRepository.findAllUniversities()
                                   .map(universityEntityToUniversityListDtoMapper::from)
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
        BinaryOperator<UniversityEntity> mergeEntity = (oldUniversity, newUniversity) -> newUniversity.logoUrl(oldUniversity.logoUrl());
        return updateEntity(requestDto, addParamFromPath, mergeEntity);
    }

    @Transactional
    public Mono<Boolean> deleteUniversity(int universityId) {
        return facultyService.deleteAllFacultiesByUniversityId(universityId)
                             .thenMany(documentService.deleteAllDocumentsByUniversityId(universityId))
                             .thenMany(buildingService.deleteAllBuildingsByUniversityId(universityId))
                             .thenMany(postService.deleteAllPostsByUniversityId(universityId))
                             .thenMany(employeeService.deleteAllEmployeesByUniversityId(universityId))
                             .then(super.deleteEntity(universityId));
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
