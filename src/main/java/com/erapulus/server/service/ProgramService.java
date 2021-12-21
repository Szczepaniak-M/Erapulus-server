package com.erapulus.server.service;

import com.erapulus.server.database.model.ProgramEntity;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.erapulus.server.database.model.FacultyEntity;
import com.erapulus.server.database.repository.FacultyRepository;
import com.erapulus.server.database.repository.ProgramRepository;
import com.erapulus.server.dto.ProgramRequestDto;
import com.erapulus.server.dto.ProgramResponseDto;
import com.erapulus.server.service.exception.NoSuchParentElementException;
import com.erapulus.server.web.common.PageablePayload;
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
                .then(programRepository.findByFacultyId(facultyId, pageRequest.getOffset(), pageRequest.getPageSize())
                                       .map(entityToResponseDtoMapper::from)
                                       .collectList()
                                       .zipWith(programRepository.countByFacultyId(facultyId))
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
