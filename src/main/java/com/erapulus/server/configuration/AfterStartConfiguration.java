package com.erapulus.server.configuration;

import com.erapulus.server.dto.employee.EmployeeCreateRequestDto;
import com.erapulus.server.service.RegisterService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@AllArgsConstructor
public class AfterStartConfiguration {

    private final ErapulusProperties erapulusProperties;
    private final RegisterService registerService;

    @EventListener
    public void createAdministratorAfterStartUp(ContextRefreshedEvent event) {
        ErapulusProperties.AdministratorProperties administrator = erapulusProperties.administrator();
        EmployeeCreateRequestDto administratorDto = EmployeeCreateRequestDto.builder()
                                                                            .firstName(administrator.firstName())
                                                                            .lastName(administrator.lastName())
                                                                            .email(administrator.email())
                                                                            .password(administrator.password())
                                                                            .build();
        administrator.password("");
        registerService.createAdministrator(administratorDto)
                       .onErrorResume(DataIntegrityViolationException.class, e -> Mono.empty())
                       .subscribe();
    }
}
