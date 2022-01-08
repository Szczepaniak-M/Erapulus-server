package com.erapulus.server.faculty.service;

import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.common.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.common.service.CrudGenericService;
import com.erapulus.server.common.web.PageablePayload;
import com.erapulus.server.faculty.database.FacultyEntity;
import com.erapulus.server.faculty.database.FacultyRepository;
import com.erapulus.server.faculty.dto.FacultyRequestDto;
import com.erapulus.server.faculty.dto.FacultyResponseDto;
import com.erapulus.server.program.service.ProgramService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.erapulus.server.common.service.QueryParamParser.parseString;

@Service
@Validated
public class FacultyService extends CrudGenericService<FacultyEntity, FacultyRequestDto, FacultyResponseDto> {

    private final FacultyRepository facultyRepository;
    private final ProgramService programService;

    public FacultyService(FacultyRepository facultyRepository,
                          RequestDtoToEntityMapper<FacultyRequestDto, FacultyEntity> requestDtoToEntityMapper,
                          EntityToResponseDtoMapper<FacultyEntity, FacultyResponseDto> entityToResponseDtoMapper,
                          ProgramService programService) {
        super(facultyRepository, requestDtoToEntityMapper, entityToResponseDtoMapper, "faculty");
        this.facultyRepository = facultyRepository;
        this.programService = programService;
    }

    public Mono<PageablePayload<FacultyResponseDto>> listFaculties(int universityId, String name, PageRequest pageRequest) {
        String parsedName = parseString(name);
        return facultyRepository.findByUniversityIdAndName(universityId, parsedName, pageRequest.getOffset(), pageRequest.getPageSize())
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
        Supplier<Mono<FacultyEntity>> supplier = () -> facultyRepository.findByIdAndUniversityId(facultyId, universityId);
        return updateEntity(requestDto, addParamFromPath, supplier);
    }

    @Transactional
    public Mono<Boolean> deleteFaculty(int facultyId, int universityId) {
        return deleteFacultyNoTransactional(facultyId, universityId);
    }

    public Flux<Boolean> deleteAllFacultiesByUniversityId(int universityId) {
        return facultyRepository.findAllByUniversityId(universityId)
                                .flatMap(facultyId -> deleteFacultyNoTransactional(facultyId, universityId));
    }

    private Mono<Boolean> deleteFacultyNoTransactional(int facultyId, int universityId) {
        Supplier<Mono<FacultyEntity>> supplier = () -> facultyRepository.findByIdAndUniversityId(facultyId, universityId);
        return programService.deleteAllProgramsByFacultyIdAndUniversityId(facultyId, universityId)
                             .then(deleteEntity(supplier));
    }
}
