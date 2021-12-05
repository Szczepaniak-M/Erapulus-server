package com.erapulus.server.service;

import com.erapulus.server.database.model.FacultyEntity;
import com.erapulus.server.database.repository.FacultyRepository;
import com.erapulus.server.dto.FacultyResponseDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.erapulus.server.dto.FacultyRequestDto;
import com.erapulus.server.web.common.PageablePayload;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@Service
@Validated
public class FacultyService extends CrudGenericService<FacultyEntity, FacultyRequestDto, FacultyResponseDto> {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository,
                          RequestDtoToEntityMapper<FacultyRequestDto, FacultyEntity> requestDtoToEntityMapper,
                          EntityToResponseDtoMapper<FacultyEntity, FacultyResponseDto> entityToResponseDtoMapper) {
        super(facultyRepository, requestDtoToEntityMapper, entityToResponseDtoMapper);
        this.facultyRepository = facultyRepository;
    }

    public Mono<PageablePayload<FacultyResponseDto>> listEntities(int universityId, PageRequest pageRequest) {
        return facultyRepository.findByUniversityId(universityId, pageRequest.getOffset(), pageRequest.getPageSize())
                                .map(entityToResponseDtoMapper::from)
                                .collectList()
                                .zipWith(facultyRepository.countByUniversityId(universityId))
                                .map(dtoListAndTotalCount -> new PageablePayload<>(dtoListAndTotalCount.getT1(), pageRequest, dtoListAndTotalCount.getT2()));
    }


    public Mono<FacultyResponseDto> createEntity(FacultyRequestDto requestDto, int universityId) {
        UnaryOperator<FacultyEntity> addParamFromPath = faculty -> faculty.universityId(universityId);
        return createEntity(requestDto, addParamFromPath);
    }

    public Mono<FacultyResponseDto> getEntityById(int facultyId, int universityId) {
        Supplier<Mono<FacultyEntity>> supplier = () -> facultyRepository.findByIdAndUniversityId(facultyId, universityId);
        return getEntityById(supplier);
    }


    public Mono<FacultyResponseDto> updateEntity(FacultyRequestDto requestDto, int facultyId, int universityId) {
        UnaryOperator<FacultyEntity> addParamFromPath = faculty -> faculty.id(facultyId).universityId(universityId);
        return updateEntity(requestDto, addParamFromPath);
    }
}
