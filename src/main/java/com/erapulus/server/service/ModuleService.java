package com.erapulus.server.service;

import com.erapulus.server.database.model.ModuleEntity;
import com.erapulus.server.database.model.ProgramEntity;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.erapulus.server.database.repository.ModuleRepository;
import com.erapulus.server.database.repository.ProgramRepository;
import com.erapulus.server.dto.ModuleRequestDto;
import com.erapulus.server.dto.ModuleResponseDto;
import com.erapulus.server.service.exception.NoSuchParentElementException;
import com.erapulus.server.web.common.PageablePayload;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.erapulus.server.service.QueryParamParser.parseString;

@Service
@Validated
public class ModuleService extends CrudGenericService<ModuleEntity, ModuleRequestDto, ModuleResponseDto> {

    private final ModuleRepository moduleRepository;
    private final ProgramRepository programRepository;

    public ModuleService(ModuleRepository moduleRepository,
                         ProgramRepository programRepository,
                         RequestDtoToEntityMapper<ModuleRequestDto, ModuleEntity> requestDtoToEntityMapper,
                         EntityToResponseDtoMapper<ModuleEntity, ModuleResponseDto> entityToResponseDtoMapper) {
        super(moduleRepository, requestDtoToEntityMapper, entityToResponseDtoMapper);
        this.moduleRepository = moduleRepository;
        this.programRepository = programRepository;
    }

    public Mono<PageablePayload<ModuleResponseDto>> listEntities(int universityId, int facultyId, int programId, String name, PageRequest pageRequest) {
        String parsedName = parseString(name);
        return checkIfProgramExists(universityId, facultyId, programId)
                .then(moduleRepository.findByProgramIdAndName(programId, parsedName, pageRequest.getOffset(), pageRequest.getPageSize())
                                      .map(entityToResponseDtoMapper::from)
                                      .collectList()
                                      .zipWith(moduleRepository.countByProgramIdAndName(programId, parsedName))
                                      .map(dtoListAndTotalCount -> new PageablePayload<>(dtoListAndTotalCount.getT1(), pageRequest, dtoListAndTotalCount.getT2())));
    }


    public Mono<ModuleResponseDto> createEntity(@Valid ModuleRequestDto requestDto, int universityId, int facultyId, int programId) {
        UnaryOperator<ModuleEntity> addParamFromPath = module -> module.programId(programId);
        return checkIfProgramExists(universityId, facultyId, programId)
                .then(createEntity(requestDto, addParamFromPath));
    }

    public Mono<ModuleResponseDto> getEntityById(int moduleId, int universityId, int facultyId, int programId) {
        Supplier<Mono<ModuleEntity>> supplier = () -> moduleRepository.findByIdAndProgramId(moduleId, programId);
        return checkIfProgramExists(universityId, facultyId, programId)
                .then(getEntityById(supplier));
    }

    public Mono<ModuleResponseDto> updateEntity(@Valid ModuleRequestDto requestDto, int moduleId, int universityId, int facultyId, int programId) {
        UnaryOperator<ModuleEntity> addParamFromPath = module -> module.id(moduleId).programId(programId);
        return checkIfProgramExists(universityId, facultyId, programId)
                .then(updateEntity(requestDto, addParamFromPath));
    }

    private Mono<ProgramEntity> checkIfProgramExists(int universityId, int facultyId, int programId) {
        return programRepository.findByIdAndUniversityIdAndFacultyId(programId, universityId, facultyId)
                                .switchIfEmpty(Mono.error(new NoSuchParentElementException()));
    }
}
