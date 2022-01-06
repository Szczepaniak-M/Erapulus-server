package com.erapulus.server.applicationuser.service;

import com.erapulus.server.applicationuser.database.ApplicationUserEntity;
import com.erapulus.server.applicationuser.database.ApplicationUserRepository;
import com.erapulus.server.applicationuser.dto.ApplicationUserDto;
import com.erapulus.server.applicationuser.mapper.ApplicationUserEntityToDtoMapper;
import com.erapulus.server.common.database.UserType;
import com.erapulus.server.common.web.PageablePayload;
import com.erapulus.server.device.service.DeviceService;
import com.erapulus.server.friendship.service.FriendshipService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

import static com.erapulus.server.common.service.QueryParamParser.*;

@Service
@AllArgsConstructor
public class ApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;
    private final DeviceService deviceService;
    private final FriendshipService friendshipService;
    private final ApplicationUserEntityToDtoMapper applicationUserEntityToDtoMapper;

    public Mono<PageablePayload<ApplicationUserDto>> listApplicationUsers(String universityId, String userType, String name, String email, PageRequest pageRequest) {
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

    @Transactional
    public Mono<Boolean> deleteApplicationUser(int userId) {
        return applicationUserRepository.findById(userId)
                                        .switchIfEmpty(Mono.error(new NoSuchElementException("user")))
                                        .flatMap(this::deleteApplicationUser)
                                        .thenReturn(true);
    }

    private Mono<Void> deleteApplicationUser(ApplicationUserEntity applicationUserEntity) {
        if (applicationUserEntity.type() == UserType.STUDENT) {
            return deviceService.deleteAllDevicesByStudentId(applicationUserEntity.id())
                                .then(friendshipService.deleteAllFriendsByStudentId(applicationUserEntity.id()))
                                .then(applicationUserRepository.delete(applicationUserEntity));
        } else {
            return applicationUserRepository.delete(applicationUserEntity);
        }
    }


}
