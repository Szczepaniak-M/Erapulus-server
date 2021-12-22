package com.erapulus.server.service;

import com.erapulus.server.database.model.UserType;
import com.erapulus.server.database.repository.ApplicationUserRepository;
import com.erapulus.server.dto.ApplicationUserDto;
import com.erapulus.server.mapper.ApplicationUserEntityToDtoMapper;
import com.erapulus.server.web.common.PageablePayload;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.erapulus.server.service.QueryParamParser.*;

@Service
@AllArgsConstructor
public class ApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;
    private final ApplicationUserEntityToDtoMapper applicationUserEntityToDtoMapper;

    public Mono<PageablePayload<ApplicationUserDto>> listEntities(String universityId, String userType, String name, String email, PageRequest pageRequest) {
        Integer universityParsed;
        UserType userTypeParsed;
        String nameParsed;
        String emailParsed;
        try {
            universityParsed = parseInteger(universityId);
            userTypeParsed = parseUseType(userType);
            nameParsed = parseString(name);
            emailParsed = parseString(email);
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalArgumentException());
        }
        return applicationUserRepository.findAllByFilters(universityParsed, userTypeParsed, nameParsed, emailParsed, pageRequest.getOffset(), pageRequest.getPageSize())
                                        .map(applicationUserEntityToDtoMapper::from)
                                        .collectList()
                                        .zipWith(applicationUserRepository.countByFilters(universityParsed, userTypeParsed, nameParsed, emailParsed))
                                        .map(dtoListAndTotalCount -> new PageablePayload<>(dtoListAndTotalCount.getT1(), pageRequest, dtoListAndTotalCount.getT2()));
    }


}
