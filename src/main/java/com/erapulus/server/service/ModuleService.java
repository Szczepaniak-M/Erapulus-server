package com.erapulus.server.service;

import com.erapulus.server.database.model.ModuleEntity;
import com.erapulus.server.database.repository.ModuleRepository;
import com.erapulus.server.database.repository.ProgramRepository;
import com.erapulus.server.dto.module.ModuleRequestDto;
import com.erapulus.server.dto.module.ModuleResponseDto;
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
public class ModuleService extends CrudGenericService<ModuleEntity, ModuleRequestDto, ModuleResponseDto> {

    private final ModuleRepository moduleRepository;
    private final ProgramRepository programRepository;
    private final DocumentService documentService;

    public ModuleService(ModuleRepository moduleRepository,
                         ProgramRepository programRepository,
                         RequestDtoToEntityMapper<ModuleRequestDto, ModuleEntity> requestDtoToEntityMapper,
                         EntityToResponseDtoMapper<ModuleEntity, ModuleResponseDto> entityToResponseDtoMapper,
                         DocumentService documentService) {
        super(moduleRepository, requestDtoToEntityMapper, entityToResponseDtoMapper, "module");
        this.moduleRepository = moduleRepository;
        this.programRepository = programRepository;
        this.documentService = documentService;
    }

    public Mono<PageablePayload<ModuleResponseDto>> listModules(int universityId, int facultyId, int programId, String name, PageRequest pageRequest) {
        String parsedName = parseString(name);
        return checkIfProgramExists(universityId, facultyId, programId)
                .then(moduleRepository.findByProgramIdAndName(programId, parsedName, pageRequest.getOffset(), pageRequest.getPageSize())
                                      .map(entityToResponseDtoMapper::from)
                                      .collectList()
                                      .zipWith(moduleRepository.countByProgramIdAndName(programId, parsedName))
                                      .map(dtoListAndTotalCount -> new PageablePayload<>(dtoListAndTotalCount.getT1(), pageRequest, dtoListAndTotalCount.getT2())));
    }


    public Mono<ModuleResponseDto> createModule(@Valid ModuleRequestDto requestDto, int universityId, int facultyId, int programId) {
        UnaryOperator<ModuleEntity> addParamFromPath = module -> module.programId(programId);
        return checkIfProgramExists(universityId, facultyId, programId)
                .then(createEntity(requestDto, addParamFromPath));
    }

    public Mono<ModuleResponseDto> getModuleById(int moduleId, int universityId, int facultyId, int programId) {
        Supplier<Mono<ModuleEntity>> supplier = () -> moduleRepository.findByIdAndProgramId(moduleId, programId);
        return checkIfProgramExists(universityId, facultyId, programId)
                .then(getEntityById(supplier));
    }

    public Mono<ModuleResponseDto> updateModule(@Valid ModuleRequestDto requestDto, int moduleId, int universityId, int facultyId, int programId) {
        UnaryOperator<ModuleEntity> addParamFromPath = module -> module.id(moduleId).programId(programId);
        return checkIfProgramExists(universityId, facultyId, programId)
                .then(updateEntity(requestDto, addParamFromPath));
    }

    @Transactional
    public Mono<Boolean> deleteModule(int moduleId, int universityId, int facultyId, int programId) {
        return checkIfProgramExists(universityId, facultyId, programId)
                .thenMany(documentService.deleteAllDocumentsByModuleId(moduleId))
                .then(super.deleteEntity(moduleId));
    }

    public Flux<Boolean> deleteAllModuleByProgramId(int programId) {
        return moduleRepository.findAllByProgramId(programId)
                               .flatMap(moduleId -> documentService.deleteAllDocumentsByModuleId(moduleId)
                                                                   .then(super.deleteEntity(moduleId)));
    }

    private Mono<Boolean> checkIfProgramExists(int universityId, int facultyId, int programId) {
        return programRepository.existsByIdAndUniversityIdAndFacultyId(programId, universityId, facultyId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException("program")));
    }
}
