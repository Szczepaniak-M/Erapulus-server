package com.erapulus.server.program.service;

import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.common.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.common.service.CrudGenericService;
import com.erapulus.server.common.web.PageablePayload;
import com.erapulus.server.document.service.DocumentService;
import com.erapulus.server.faculty.database.FacultyEntity;
import com.erapulus.server.faculty.database.FacultyRepository;
import com.erapulus.server.module.service.ModuleService;
import com.erapulus.server.program.database.ProgramEntity;
import com.erapulus.server.program.database.ProgramRepository;
import com.erapulus.server.program.dto.ProgramRequestDto;
import com.erapulus.server.program.dto.ProgramResponseDto;
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

import static com.erapulus.server.common.service.QueryParamParser.parseString;

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
        Supplier<Mono<ProgramEntity>> supplier = () -> programRepository.findByIdAndUniversityIdAndFacultyId(programId, universityId, facultyId);
        return updateEntity(requestDto, addParamFromPath, supplier);
    }

    @Transactional
    public Mono<Boolean> deleteProgram(int programId, int universityId, int facultyId) {
        return deleteProgramNoTransactional(programId, universityId, facultyId);
    }

    public Flux<Boolean> deleteAllProgramsByFacultyIdAndUniversityId(int facultyId, int universityId) {
        return programRepository.findAllByFacultyId(facultyId)
                                .flatMap(programId -> deleteProgramNoTransactional(programId, universityId, facultyId));
    }

    private Mono<Boolean> deleteProgramNoTransactional(int programId, int universityId, int facultyId) {
        Supplier<Mono<ProgramEntity>> supplier = () -> programRepository.findByIdAndUniversityIdAndFacultyId(programId, universityId, facultyId);
        return moduleService.deleteAllModuleByProgramId(programId)
                            .thenMany(documentService.deleteAllDocumentsByProgramId(programId))
                            .then(deleteEntity(supplier));
    }

    private Mono<FacultyEntity> checkIfFacultyExists(int universityId, int facultyId) {
        return facultyRepository.findByIdAndUniversityId(facultyId, universityId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException("faculty")));
    }
}
