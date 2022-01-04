package com.erapulus.server.service;

import com.erapulus.server.database.model.FacultyEntity;
import com.erapulus.server.database.repository.FacultyRepository;
import com.erapulus.server.database.repository.UniversityRepository;
import com.erapulus.server.dto.faculty.FacultyRequestDto;
import com.erapulus.server.dto.faculty.FacultyResponseDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.web.common.PageablePayload;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.erapulus.server.service.QueryParamParser.parseString;

@Service
@Validated
public class FacultyService extends CrudGenericService<FacultyEntity, FacultyRequestDto, FacultyResponseDto> {

    private final FacultyRepository facultyRepository;
    private final UniversityRepository universityRepository;
    private final ProgramService programService;

    public FacultyService(FacultyRepository facultyRepository,
                          RequestDtoToEntityMapper<FacultyRequestDto, FacultyEntity> requestDtoToEntityMapper,
                          EntityToResponseDtoMapper<FacultyEntity, FacultyResponseDto> entityToResponseDtoMapper,
                          UniversityRepository universityRepository,
                          ProgramService programService) {
        super(facultyRepository, requestDtoToEntityMapper, entityToResponseDtoMapper, "faculty");
        this.facultyRepository = facultyRepository;
        this.universityRepository = universityRepository;
        this.programService = programService;
    }

    public Mono<PageablePayload<FacultyResponseDto>> listFaculties(int universityId, String name, PageRequest pageRequest) {
        String parsedName = parseString(name);
        return universityRepository.existsById(universityId)
                                   .switchIfEmpty(Mono.error(new NoSuchElementException("university")))
                                   .thenMany(facultyRepository.findByUniversityIdAndName(universityId, parsedName, pageRequest.getOffset(), pageRequest.getPageSize()))
                                   .map(entityToResponseDtoMapper::from)
                                   .collectList()
                                   .zipWith(facultyRepository.countByUniversityIdAndName(universityId, parsedName))
                                   .map(dtoListAndTotalCount -> new PageablePayload<>(dtoListAndTotalCount.getT1(), pageRequest, dtoListAndTotalCount.getT2()));
    }


    public Mono<FacultyResponseDto> createFaculty(FacultyRequestDto requestDto, int universityId) {
        UnaryOperator<FacultyEntity> addParamFromPath = faculty -> faculty.universityId(universityId);
        return createEntity(requestDto, addParamFromPath);
    }

    public Mono<FacultyResponseDto> getFacultyById(int facultyId, int universityId) {
        Supplier<Mono<FacultyEntity>> supplier = () -> facultyRepository.findByIdAndUniversityId(facultyId, universityId);
        return getEntityById(supplier);
    }


    public Mono<FacultyResponseDto> updateFaculty(FacultyRequestDto requestDto, int facultyId, int universityId) {
        UnaryOperator<FacultyEntity> addParamFromPath = faculty -> faculty.id(facultyId).universityId(universityId);
        return updateEntity(requestDto, addParamFromPath);
    }

    @Transactional
    public Mono<Boolean> deleteFaculty(int facultyId) {
        return programService.deleteAllProgramsByFacultyId(facultyId)
                .then(super.deleteEntity(facultyId));
    }

    public Flux<Boolean> deleteAllFacultiesByUniversityId(int universityId) {
        return facultyRepository.findAllByUniversityId(universityId)
                                .flatMap(this::deleteFaculty);
    }
}
