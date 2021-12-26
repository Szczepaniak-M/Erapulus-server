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

    public EmployeeService(EmployeeRepository employeeRepository,
                           RequestDtoToEntityMapper<EmployeeRequestDto, EmployeeEntity> requestDtoToEntityMapper,
                           EntityToResponseDtoMapper<EmployeeEntity, EmployeeResponseDto> entityToResponseDtoMapper) {
        super(employeeRepository, requestDtoToEntityMapper, entityToResponseDtoMapper, "employee");
        this.employeeRepository = employeeRepository;
    }

    public Mono<List<EmployeeResponseDto>> listEntities(Integer universityId) {
        return employeeRepository.findAllByUniversityIdAndType(universityId)
                                 .map(entityToResponseDtoMapper::from)
                                 .collectList();
    }

    public Mono<EmployeeResponseDto> getEntityById(int employeeId) {
        Supplier<Mono<EmployeeEntity>> supplier = () -> employeeRepository.findByIdAndType(employeeId);
        return getEntityById(supplier);
    }

    public Mono<EmployeeResponseDto> updateEntity(@Valid EmployeeRequestDto requestDto, int employeeId) {
        return Mono.just(requestDto)
                   .map(requestDtoToEntityMapper::from)
                   .map(employee -> employee.id(employeeId))
                   .flatMap(updatedT -> employeeRepository.findById(updatedT.id())
                                                          .switchIfEmpty(Mono.error(new NoSuchElementException(entityName)))
                                                          .flatMap(oldEntity -> employeeRepository.save(updatedT.type(oldEntity.type())
                                                                                                                .password(oldEntity.password())
                                                                                                                .universityId(oldEntity.universityId()))))
                   .map(entityToResponseDtoMapper::from);
    }
}
