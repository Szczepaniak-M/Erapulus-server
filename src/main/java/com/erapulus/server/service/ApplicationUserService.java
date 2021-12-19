package com.erapulus.server.service;

import com.erapulus.server.database.model.UserType;
import com.erapulus.server.database.repository.UserRepository;
import com.erapulus.server.dto.ApplicationUserDto;
import com.erapulus.server.mapper.ApplicationUserEntityToDtoMapper;
import com.erapulus.server.web.common.PageablePayload;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.erapulus.server.service.QueryParamParser.*;

@Service
@AllArgsConstructor
public class ApplicationUserService {

    private final UserRepository userRepository;
    private final ApplicationUserEntityToDtoMapper applicationUserEntityToDtoMapper;

    public Mono<PageablePayload<ApplicationUserDto>> listEntities(String universityId, String userType, String name, PageRequest pageRequest) {
        Integer universityParsed;
        UserType userTypeParsed;
        String nameParsed;
        try {
            universityParsed = parseInteger(universityId);
            userTypeParsed = parseUseType(userType);
            nameParsed = parseString(name);
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalArgumentException());
        }
        return userRepository.findByFilters(universityParsed, userTypeParsed, nameParsed, pageRequest.getOffset(), pageRequest.getPageSize())
                             .map(applicationUserEntityToDtoMapper::from)
                             .collectList()
                             .zipWith(userRepository.countByFilters(universityParsed, userTypeParsed, nameParsed))
                             .map(dtoListAndTotalCount -> new PageablePayload<>(dtoListAndTotalCount.getT1(), pageRequest, dtoListAndTotalCount.getT2()));
    }


}
