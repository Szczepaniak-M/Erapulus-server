package com.erapulus.server.employee.service;

import com.erapulus.server.applicationuser.database.ApplicationUserEntity;
import com.erapulus.server.common.database.UserType;
import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.common.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.common.service.CrudGenericService;
import com.erapulus.server.employee.database.EmployeeEntity;
import com.erapulus.server.employee.database.EmployeeRepository;
import com.erapulus.server.employee.dto.EmployeeRequestDto;
import com.erapulus.server.employee.dto.EmployeeResponseDto;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.erapulus.server.security.SecurityContextUtils.withSecurityContext;

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

    public Mono<List<EmployeeResponseDto>> listEmployees(Integer universityId) {
        return employeeRepository.findAllByUniversityIdAndType(universityId)
                                 .map(entityToResponseDtoMapper::from)
                                 .collectList();
    }

    public Mono<EmployeeResponseDto> getEmployeeById(int employeeId) {
        Supplier<Mono<EmployeeEntity>> supplier = () -> employeeRepository.findByIdAndType(employeeId);
        return getEntityById(supplier)
                .flatMap(this::validateBodyContent);
    }

    @Transactional
    public Mono<EmployeeResponseDto> updateEmployee(@Valid EmployeeRequestDto requestDto, int employeeId) {
        UnaryOperator<EmployeeEntity> addParamFromPath = employee -> employee.id(employeeId);
        Supplier<Mono<EmployeeEntity>> supplier = () -> employeeRepository.findByIdAndType(employeeId);
        BinaryOperator<EmployeeEntity> mergeEntity = (oldEmployee, newEmployee) -> newEmployee.type(oldEmployee.type())
                                                                                              .password(oldEmployee.password())
                                                                                              .universityId(oldEmployee.universityId());
        return updateEntity(requestDto, addParamFromPath, supplier, mergeEntity)
                .flatMap(this::validateBodyContent);
    }

    public Mono<Void> deleteAllEmployeesByUniversityId(int universityId) {
        return employeeRepository.deleteAllByUniversityId(universityId);
    }

    private Mono<EmployeeResponseDto> validateBodyContent(EmployeeResponseDto employeeResponseDto) {
        return withSecurityContext(user -> validateBodyContent(employeeResponseDto, user));
    }

    private Mono<EmployeeResponseDto> validateBodyContent(EmployeeResponseDto employeeResponseDto, ApplicationUserEntity user) {
        if ((user.type() == UserType.UNIVERSITY_ADMINISTRATOR || user.type() == UserType.EMPLOYEE)
                && !Objects.equals(user.universityId(), employeeResponseDto.universityId())) {
            return Mono.error(new AccessDeniedException("access.denied"));
        }
        if (user.type() == UserType.ADMINISTRATOR && employeeResponseDto.type() == UserType.EMPLOYEE) {
            return Mono.error(new AccessDeniedException("access.denied"));
        }
        return Mono.just(employeeResponseDto);
    }
}
