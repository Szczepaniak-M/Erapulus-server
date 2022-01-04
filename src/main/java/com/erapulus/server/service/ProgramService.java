package com.erapulus.server.service;

import com.erapulus.server.database.model.FacultyEntity;
import com.erapulus.server.database.model.ProgramEntity;
import com.erapulus.server.database.repository.FacultyRepository;
import com.erapulus.server.database.repository.ProgramRepository;
import com.erapulus.server.dto.program.ProgramRequestDto;
import com.erapulus.server.dto.program.ProgramResponseDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.web.common.PageablePayload;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.erapulus.server.service.QueryParamParser.parseString;

@Service
@Validated
public class ProgramService extends CrudGenericService<ProgramEntity, ProgramRequestDto, ProgramResponseDto> {
    private final ProgramRepository programRepository;
    private final FacultyRepository facultyRepository;
    private final DocumentService documentService;
    private final ModuleService moduleService;

    public ProgramService(ProgramRepository programRepository,
                          FacultyRepository facultyRepository,
                          RequestDtoToEntityMapper<ProgramRequestDto, ProgramEntity> requestDtoToEntityMapper,
                          EntityToResponseDtoMapper<ProgramEntity, ProgramResponseDto> entityToResponseDtoMapper,
                          DocumentService documentService,
                          ModuleService moduleService) {
        super(programRepository, requestDtoToEntityMapper, entityToResponseDtoMapper, "program");
        this.programRepository = programRepository;
        this.facultyRepository = facultyRepository;
        this.documentService = documentService;
        this.moduleService = moduleService;
    }

    public Mono<PageablePayload<ProgramResponseDto>> listPrograms(int universityId, int facultyId, String name, PageRequest pageRequest) {
        String parsedName = parseString(name);
        return checkIfFacultyExists(universityId, facultyId)
                .then(programRepository.findByFacultyIdAndName(facultyId, parsedName, pageRequest.getOffset(), pageRequest.getPageSize())
                                       .map(entityToResponseDtoMapper::from)
                                       .collectList()
                                       .zipWith(programRepository.countByFacultyIdAndName(facultyId, parsedName))
                                       .map(dtoListAndTotalCount -> new PageablePayload<>(dtoListAndTotalCount.getT1(), pageRequest, dtoListAndTotalCount.getT2())));
    }

    public Mono<ProgramResponseDto> createProgram(@Valid ProgramRequestDto requestDto, int universityId, int facultyId) {
        UnaryOperator<ProgramEntity> addParamFromPath = moduleEntity -> moduleEntity.facultyId(facultyId);
        return checkIfFacultyExists(universityId, facultyId)
                .then(createEntity(requestDto, addParamFromPath));
    }

    public Mono<ProgramResponseDto> getProgramById(int programId, int universityId, int facultyId) {
        Supplier<Mono<ProgramEntity>> supplier = () -> programRepository.findByIdAndUniversityIdAndFacultyId(programId, universityId, facultyId);
        return getEntityById(supplier);
    }

    public Mono<ProgramResponseDto> updateProgram(@Valid ProgramRequestDto requestDto, int programId, int universityId, int facultyId) {
        UnaryOperator<ProgramEntity> addParamFromPath = program -> program.id(programId).facultyId(facultyId);
        return checkIfFacultyExists(universityId, facultyId)
                .then(updateEntity(requestDto, addParamFromPath));
    }

    @Transactional
    public Mono<Boolean> deleteProgram(int programId, int universityId, int facultyId) {
        return checkIfFacultyExists(universityId, facultyId)
                .thenMany(moduleService.deleteAllModuleByProgramId(programId))
                .thenMany(documentService.deleteAllDocumentsByProgramId(programId))
                .then(super.deleteEntity(programId));
    }

    public Flux<Boolean> deleteAllProgramsByFacultyId(int facultyId) {
        return programRepository.findAllByFacultyId(facultyId)
                                .flatMap(programId -> documentService.deleteAllDocumentsByProgramId(programId)
                                                                     .thenMany(moduleService.deleteAllModuleByProgramId(programId))
                                                                     .then(super.deleteEntity(programId)));
    }

    private Mono<FacultyEntity> checkIfFacultyExists(int universityId, int facultyId) {
        return facultyRepository.findByIdAndUniversityId(facultyId, universityId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException("faculty")));
    }
}
