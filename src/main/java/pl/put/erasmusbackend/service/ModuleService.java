package pl.put.erasmusbackend.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.put.erasmusbackend.database.model.ModuleEntity;
import pl.put.erasmusbackend.database.model.ProgramEntity;
import pl.put.erasmusbackend.database.repository.ModuleRepository;
import pl.put.erasmusbackend.database.repository.ProgramRepository;
import pl.put.erasmusbackend.dto.ModuleRequestDto;
import pl.put.erasmusbackend.dto.ModuleResponseDto;
import pl.put.erasmusbackend.mapper.EntityToResponseDtoMapper;
import pl.put.erasmusbackend.mapper.RequestDtoToEntityMapper;
import pl.put.erasmusbackend.service.exception.NoSuchParentElementException;
import pl.put.erasmusbackend.web.common.PageablePayload;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

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

    public Mono<PageablePayload<ModuleResponseDto>> listEntities(int universityId, int facultyId, int programId, PageRequest pageRequest) {
        return checkIfProgramExists(universityId, facultyId, programId)
                .then(moduleRepository.findByProgramId(programId, pageRequest.getOffset(), pageRequest.getPageSize())
                                      .map(entityToResponseDtoMapper::from)
                                      .collectList()
                                      .zipWith(moduleRepository.countByProgramId(programId))
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
