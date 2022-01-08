package com.erapulus.server.applicationuser.service;

import com.erapulus.server.applicationuser.database.ApplicationUserEntity;
import com.erapulus.server.applicationuser.database.ApplicationUserRepository;
import com.erapulus.server.applicationuser.dto.ApplicationUserDto;
import com.erapulus.server.applicationuser.mapper.ApplicationUserEntityToDtoMapper;
import com.erapulus.server.common.database.UserType;
import com.erapulus.server.common.web.PageablePayload;
import com.erapulus.server.device.service.DeviceService;
import com.erapulus.server.friendship.service.FriendshipService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationUserServiceTest {

    public static final int ID = 11;

    @Mock
    ApplicationUserRepository applicationUserRepository;

    @Mock
    DeviceService deviceService;

    @Mock
    FriendshipService friendshipService;

    ApplicationUserService applicationUserService;

    @BeforeEach
    void setUp() {
        applicationUserService = new ApplicationUserService(applicationUserRepository, deviceService, friendshipService, new ApplicationUserEntityToDtoMapper());
    }

    @Test
    void listApplicationUsers_shouldPageableListWhenCorrectInput() {
        // given
        var studentEntity = createUser(UserType.STUDENT);
        var studentDto = createUserDto();
        String universityId = "1";
        String userType = "STUDENT";
        String name = "John";
        String email = "";
        PageRequest pageRequest = PageRequest.of(1, 10);
        when(applicationUserRepository.findAllByFilters(1, UserType.STUDENT, name, null, pageRequest.getOffset(), pageRequest.getPageSize()))
                .thenReturn(Flux.just(studentEntity));
        when(applicationUserRepository.countByFilters(1, UserType.STUDENT, name, null))
                .thenReturn(Mono.just(ApplicationUserServiceTest.ID));
        PageablePayload<ApplicationUserDto> expected = new PageablePayload<>(List.of(studentDto), pageRequest, ApplicationUserServiceTest.ID);

        // when
        Mono<PageablePayload<ApplicationUserDto>> result = applicationUserService.listApplicationUsers(universityId, userType, name, email, pageRequest);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(payload -> assertThat(payload)
                            .usingRecursiveComparison()
                            .isEqualTo(expected))
                    .verifyComplete();
    }

    @Test
    void listApplicationUsers_shouldReturnExceptionWhenWrongInput() {
        // given
        String universityId = "1a";
        String userType = "";
        String name = "";
        String email = "";
        PageRequest pageRequest = PageRequest.of(1, 10);

        // when
        Mono<PageablePayload<ApplicationUserDto>> result = applicationUserService.listApplicationUsers(universityId, userType, name, email, pageRequest);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(IllegalArgumentException.class)
                    .verify();
    }

    @Test
    void deleteApplicationUser_shouldDeleteWhenUserIsStudent() {
        // given
        var studentEntity = createUser(UserType.STUDENT);
        when(applicationUserRepository.findById(ID)).thenReturn(Mono.just(studentEntity));
        when(friendshipService.deleteAllFriendsByStudentId(ID)).thenReturn(Mono.empty());
        when(deviceService.deleteAllDevicesByStudentId(ID)).thenReturn(Mono.empty());
        when(applicationUserRepository.delete(studentEntity)).thenReturn(Mono.empty());

        // when
        Mono<Boolean> result = applicationUserService.deleteApplicationUser(ID);

        //then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertTrue)
                    .verifyComplete();
    }

    @Test
    void deleteApplicationUser_shouldDeleteWhenUserIsNotStudent() {
        // given
        var employeeEntity = createUser(UserType.EMPLOYEE);
        when(applicationUserRepository.findById(ID)).thenReturn(Mono.just(employeeEntity));
        when(applicationUserRepository.delete(employeeEntity)).thenReturn(Mono.empty());


        // when
        Mono<Boolean> result = applicationUserService.deleteApplicationUser(ID);

        //then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertTrue)
                    .verifyComplete();
    }

    @Test
    void deleteApplicationUser_shouldThrowExceptionWhenUSerNotFound() {
        // given
        var studentEntity = createUser(UserType.EMPLOYEE);
        when(applicationUserRepository.findById(ID)).thenReturn(Mono.empty());


        // when
        Mono<Boolean> result = applicationUserService.deleteApplicationUser(ID);

        //then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    private ApplicationUserEntity createUser(UserType userType) {
        return ApplicationUserEntity.builder()
                                    .id(ID)
                                    .type(userType)
                                    .firstName("John")
                                    .lastName("John")
                                    .email("example@gmail.com")
                                    .universityId(1)
                                    .build();
    }

    private ApplicationUserDto createUserDto() {
        return ApplicationUserDto.builder()
                                 .id(ApplicationUserServiceTest.ID)
                                 .firstName("John")
                                 .lastName("John")
                                 .type(UserType.STUDENT)
                                 .email("example@gmail.com")
                                 .universityId(1)
                                 .build();
    }
}
