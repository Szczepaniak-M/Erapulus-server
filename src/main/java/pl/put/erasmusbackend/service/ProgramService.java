package pl.put.erasmusbackend.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.put.erasmusbackend.database.model.FacultyEntity;
import pl.put.erasmusbackend.database.model.ProgramEntity;
import pl.put.erasmusbackend.database.repository.FacultyRepository;
import pl.put.erasmusbackend.database.repository.ProgramRepository;
import pl.put.erasmusbackend.dto.ProgramRequestDto;
import pl.put.erasmusbackend.dto.ProgramResponseDto;
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
public class ProgramService extends CrudGenericService<ProgramEntity, ProgramRequestDto, ProgramResponseDto> {
    private final ProgramRepository programRepository;
    private final FacultyRepository facultyRepository;

    public ProgramService(ProgramRepository programRepository,
                          FacultyRepository facultyRepository,
                          RequestDtoToEntityMapper<ProgramRequestDto, ProgramEntity> requestDtoToEntityMapper,
                          EntityToResponseDtoMapper<ProgramEntity, ProgramResponseDto> entityToResponseDtoMapper) {
        super(programRepository, requestDtoToEntityMapper, entityToResponseDtoMapper);
        this.programRepository = programRepository;
        this.facultyRepository = facultyRepository;
    }

    public Mono<PageablePayload<ProgramResponseDto>> listEntities(int universityId, int facultyId, PageRequest pageRequest) {
        return checkIfFacultyExists(universityId, facultyId)
                .then(programRepository.findByFaculty(facultyId, pageRequest.getOffset(), pageRequest.getPageSize())
                                       .map(entityToResponseDtoMapper::from)
                                       .collectList()
                                       .zipWith(programRepository.countByFaculty(facultyId))
                                       .map(dtoListAndTotalCount -> new PageablePayload<>(dtoListAndTotalCount.getT1(), pageRequest, dtoListAndTotalCount.getT2())));
    }

    public Mono<ProgramResponseDto> createEntity(@Valid ProgramRequestDto requestDto, int universityId, int facultyId) {
        UnaryOperator<ProgramEntity> addParamFromPath = moduleEntity -> moduleEntity.facultyId(facultyId);
        return checkIfFacultyExists(universityId, facultyId)
                .then(createEntity(requestDto, addParamFromPath));
    }

    public Mono<ProgramResponseDto> getEntityById(int programId, int universityId, int facultyId) {
        Supplier<Mono<ProgramEntity>> supplier = () -> programRepository.findByIdAndUniversityIdAndFacultyId(programId, universityId, facultyId);
        return getEntityById(supplier);
    }

    public Mono<ProgramResponseDto> updateEntity(@Valid ProgramRequestDto requestDto, int programId, int universityId, int facultyId) {
        UnaryOperator<ProgramEntity> addParamFromPath = program -> program.id(programId).facultyId(facultyId);
        return checkIfFacultyExists(universityId, facultyId)
                .then(updateEntity(requestDto, addParamFromPath));
    }

    private Mono<FacultyEntity> checkIfFacultyExists(int universityId, int facultyId) {
        return facultyRepository.findByIdAndUniversityId(facultyId, universityId)
                                .switchIfEmpty(Mono.error(new NoSuchParentElementException()));
    }
}
