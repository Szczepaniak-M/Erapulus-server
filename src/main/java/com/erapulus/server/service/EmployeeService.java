package com.erapulus.server.service;

import com.erapulus.server.database.model.EmployeeEntity;
import com.erapulus.server.database.repository.EmployeeRepository;
import com.erapulus.server.database.repository.UniversityRepository;
import com.erapulus.server.dto.EmployeeRequestDto;
import com.erapulus.server.dto.EmployeeResponseDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

@Service
@Validated
public class EmployeeService extends CrudGenericService<EmployeeEntity, EmployeeRequestDto, EmployeeResponseDto> {

    private final EmployeeRepository employeeRepository;
    private final UniversityRepository universityRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           RequestDtoToEntityMapper<EmployeeRequestDto, EmployeeEntity> requestDtoToEntityMapper,
                           EntityToResponseDtoMapper<EmployeeEntity, EmployeeResponseDto> entityToResponseDtoMapper,
                           UniversityRepository universityRepository) {
        super(employeeRepository, requestDtoToEntityMapper, entityToResponseDtoMapper);
        this.employeeRepository = employeeRepository;
        this.universityRepository = universityRepository;
    }

    public Mono<List<EmployeeResponseDto>> listEntities(Integer universityId) {
        return universityRepository.findById(universityId)
                                   .switchIfEmpty(Mono.error(new NoSuchElementException()))
                                   .thenMany(employeeRepository.findAllEmployeeByUniversityId(universityId))
                                   .map(entityToResponseDtoMapper::from)
                                   .collectList();
    }

    public Mono<EmployeeResponseDto> getEntityById(int employeeId) {
        Supplier<Mono<EmployeeEntity>> supplier = () -> employeeRepository.findById(employeeId);
        return getEntityById(supplier);
    }

    public Mono<EmployeeResponseDto> updateEntity(@Valid EmployeeRequestDto requestDto, int employeeId) {
        return Mono.just(requestDto)
                   .map(requestDtoToEntityMapper::from)
                   .map(employee -> employee.id(employeeId))
                   .flatMap(updatedT -> employeeRepository.findById(updatedT.id())
                                                          .switchIfEmpty(Mono.error(new NoSuchElementException()))
                                                          .flatMap(oldEntity -> employeeRepository.save(updatedT.type(oldEntity.type())
                                                                                                                .universityId(oldEntity.universityId()))))
                   .map(entityToResponseDtoMapper::from);
    }

    @Override
    public Mono<Boolean> deleteEntity(int employeeId) {
        return employeeRepository.findById(employeeId)
                                 .switchIfEmpty(Mono.error(new NoSuchElementException()))
                                 .flatMap(b -> employeeRepository.deleteById(employeeId))
                                 .thenReturn(true);
    }
}
