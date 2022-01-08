package com.erapulus.server.friendship.service;

import com.erapulus.server.common.web.PageablePayload;
import com.erapulus.server.friendship.database.FriendshipEntity;
import com.erapulus.server.friendship.database.FriendshipRepository;
import com.erapulus.server.friendship.database.FriendshipStatus;
import com.erapulus.server.friendship.dto.FriendshipDecisionDto;
import com.erapulus.server.friendship.dto.FriendshipRequestDto;
import com.erapulus.server.friendship.dto.FriendshipResponseDto;
import com.erapulus.server.friendship.mapper.FriendshipEntityToResponseDtoMapper;
import com.erapulus.server.student.database.StudentEntity;
import com.erapulus.server.student.database.StudentRepository;
import com.erapulus.server.student.dto.StudentListDto;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceTest {

    private final static int USER_ID = 1;
    private final static int FRIENDSHIP_ID = 2;
    private final static int FRIEND_ID_1 = 3;
    private final static int FRIEND_ID_2 = 4;


    @Mock
    FriendshipRepository friendshipRepository;

    @Mock
    StudentRepository studentRepository;

    @Mock
    PushNotificationService pushNotificationService;

    FriendshipService friendshipService;

    @BeforeEach
    void setUp() {
        friendshipService = new FriendshipService(friendshipRepository,
                studentRepository,
                pushNotificationService,
                new FriendshipEntityToResponseDtoMapper());
    }

    @Test
    void listFriends_shouldReturnFriendsList() {
        // given
        var friend1 = createStudent(FRIEND_ID_1);
        var friend2 = createStudent(FRIEND_ID_2);
        var friendDto1 = createStudentDto(FRIEND_ID_1);
        var friendDto2 = createStudentDto(FRIEND_ID_2);
        var totalCount = 12;
        String name = "";
        PageRequest pageRequest = PageRequest.of(1, 10);
        when(friendshipRepository.findFriendsByIdAndFilters(USER_ID, null, pageRequest.getOffset(), pageRequest.getPageSize()))
                .thenReturn(Flux.just(friend1, friend2));
        when(friendshipRepository.countFriendsByIdAndFilters(USER_ID, null))
                .thenReturn(Mono.just(totalCount));
        PageablePayload<StudentListDto> expected = new PageablePayload<>(List.of(friendDto1, friendDto2), pageRequest, totalCount);

        // when
        Mono<PageablePayload<StudentListDto>> result = friendshipService.listFriends(USER_ID, name, pageRequest);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(payload -> assertThat(payload)
                            .usingRecursiveComparison()
                            .isEqualTo(expected))
                    .verifyComplete();
    }

    @Test
    void listFriendRequests_shouldReturnFriendRequests() {
        // given
        var friend1 = createStudent(FRIEND_ID_1);
        var friend2 = createStudent(FRIEND_ID_2);
        when(friendshipRepository.findFriendRequestsById(USER_ID)).thenReturn(Flux.just(friend1, friend2));

        // when
        Mono<List<StudentListDto>> result = friendshipService.listFriendRequests(USER_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(friendRequests -> assertEquals(2, friendRequests.size()))
                    .verifyComplete();
    }

    @Test
    void addFriendRequest_shouldAddFriendRequest() {
        // given
        var friend = createStudent(FRIEND_ID_1);
        var friendshipRequestDto = new FriendshipRequestDto(FRIEND_ID_1);
        when(studentRepository.findByIdAndType(FRIEND_ID_1)).thenReturn(Mono.just(friend));
        when(friendshipRepository.findByUserIdAndFriendId(USER_ID, FRIEND_ID_1)).thenReturn(Mono.empty());
        when(friendshipRepository.save(any(FriendshipEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, FriendshipEntity.class).id(FRIENDSHIP_ID)));
        when(pushNotificationService.sendPushNotification(any(FriendshipResponseDto.class))).thenReturn(Mono.empty());

        // when
        Mono<FriendshipResponseDto> result = friendshipService.addFriendRequest(friendshipRequestDto, USER_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(friendRequests -> {
                        assertEquals(FRIENDSHIP_ID, friendRequests.id());
                        assertEquals(USER_ID, friendRequests.friendId());
                        assertEquals(FRIEND_ID_1, friendRequests.applicationUserId());
                        assertEquals(FriendshipStatus.REQUESTED, friendRequests.status());
                    })
                    .verifyComplete();
    }

    @Test
    void addFriendRequest_shouldThorExceptionWhenStudentInviteThemself() {
        // given
        var friendshipRequestDto = new FriendshipRequestDto(USER_ID);

        // when
        Mono<FriendshipResponseDto> result = friendshipService.addFriendRequest(friendshipRequestDto, USER_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(IllegalArgumentException.class)
                    .verify();
    }

    @Test
    void addFriendRequest_shouldThrowExceptionWhenFriendNotFound() {
        // given
        var friendshipRequestDto = new FriendshipRequestDto(FRIEND_ID_1);
        when(studentRepository.findByIdAndType(FRIEND_ID_1)).thenReturn(Mono.empty());
        when(friendshipRepository.findByUserIdAndFriendId(USER_ID, FRIEND_ID_1)).thenReturn(Mono.error(IllegalStateException::new));

        // when
        Mono<FriendshipResponseDto> result = friendshipService.addFriendRequest(friendshipRequestDto, USER_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void addFriendRequest_shouldThrowExceptionWhenFriendNRequestAlreadyExists() {
        // given
        var friend = createStudent(FRIEND_ID_1);
        var friendshipRequestDto = new FriendshipRequestDto(FRIEND_ID_1);
        when(studentRepository.findByIdAndType(FRIEND_ID_1)).thenReturn(Mono.just(friend));
        when(friendshipRepository.findByUserIdAndFriendId(USER_ID, FRIEND_ID_1)).thenReturn(Mono.just(new FriendshipEntity()));

        // when
        Mono<FriendshipResponseDto> result = friendshipService.addFriendRequest(friendshipRequestDto, USER_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(IllegalStateException.class)
                    .verify();
    }

    @Test
    void handleFriendshipRequest_shouldAcceptRequest() {
        // given
        var friendshipDecisionDto = new FriendshipDecisionDto(true);
        var friendship = new FriendshipEntity(FRIENDSHIP_ID, USER_ID, FRIEND_ID_1, FriendshipStatus.REQUESTED, null);
        when(friendshipRepository.findByUserIdAndFriendId(USER_ID, FRIEND_ID_1)).thenReturn(Mono.just(friendship));
        when(friendshipRepository.save(any(FriendshipEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, FriendshipEntity.class)));

        // when
        Mono<FriendshipResponseDto> result = friendshipService.handleFriendshipRequest(friendshipDecisionDto, USER_ID, FRIEND_ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(friendRequests -> {
                        assertNull(friendRequests.id());
                        assertEquals(FRIEND_ID_1, friendRequests.applicationUserId());
                        assertEquals(USER_ID, friendRequests.friendId());
                        assertEquals(FriendshipStatus.ACCEPTED, friendRequests.status());
                    })
                    .verifyComplete();
        verify(friendshipRepository, times(2)).save(any(FriendshipEntity.class));
    }

    @Test
    void handleFriendshipRequest_shouldThrowExceptionWhenAcceptedAndNoRequest() {
        // given
        var friendshipDecisionDto = new FriendshipDecisionDto(true);
        when(friendshipRepository.findByUserIdAndFriendId(USER_ID, FRIEND_ID_1)).thenReturn(Mono.empty());

        // when
        Mono<FriendshipResponseDto> result = friendshipService.handleFriendshipRequest(friendshipDecisionDto, USER_ID, FRIEND_ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void handleFriendshipRequest_shouldDeclineRequest() {
        // given
        var friendshipDecisionDto = new FriendshipDecisionDto(false);
        when(friendshipRepository.deleteByUserIdAndFriendId(USER_ID, FRIEND_ID_1)).thenReturn(Mono.just(1));

        // when
        Mono<FriendshipResponseDto> result = friendshipService.handleFriendshipRequest(friendshipDecisionDto, USER_ID, FRIEND_ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(friendRequests -> {
                        assertNull(friendRequests.id());
                        assertEquals(USER_ID, friendRequests.applicationUserId());
                        assertEquals(FRIEND_ID_1, friendRequests.friendId());
                        assertEquals(FriendshipStatus.DECLINED, friendRequests.status());
                    })
                    .verifyComplete();
    }

    @Test
    void handleFriendshipRequest_shouldShouldThrowExceptionWhenDeclinedAndNoRequest() {
        // given
        var friendshipDecisionDto = new FriendshipDecisionDto(false);
        when(friendshipRepository.deleteByUserIdAndFriendId(USER_ID, FRIEND_ID_1)).thenReturn(Mono.just(0));

        // when
        Mono<FriendshipResponseDto> result = friendshipService.handleFriendshipRequest(friendshipDecisionDto, USER_ID, FRIEND_ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteFriend_shouldDeleteFriend() {
        // when
        when(friendshipRepository.deleteByUserIdAndFriendId(USER_ID, FRIEND_ID_1)).thenReturn(Mono.just(2));

        // given
        Mono<Boolean> result = friendshipService.deleteFriend(USER_ID, FRIEND_ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertTrue)
                    .verifyComplete();
    }

    @Test
    void deleteFriend_shouldNotDeleteWhenOnlyRequest() {
        // when
        when(friendshipRepository.deleteByUserIdAndFriendId(USER_ID, FRIEND_ID_1)).thenReturn(Mono.just(1));

        // given
        Mono<Boolean> result = friendshipService.deleteFriend(USER_ID, FRIEND_ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteFriend_shouldNotDeleteWhenNoFriendship() {
        // when
        when(friendshipRepository.deleteByUserIdAndFriendId(USER_ID, FRIEND_ID_1)).thenReturn(Mono.just(0));

        // given
        Mono<Boolean> result = friendshipService.deleteFriend(USER_ID, FRIEND_ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteAllFriendsByStudentId() {
        // when
        when(friendshipRepository.deleteAllByStudentId(USER_ID)).thenReturn(Mono.empty());

        // given
        Mono<Void> result = friendshipService.deleteAllFriendsByStudentId(USER_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    private StudentEntity createStudent(int id) {
        return StudentEntity.builder()
                            .id(id)
                            .firstName("John")
                            .lastName("John")
                            .email("example@gmail.com")
                            .universityId(1)
                            .pictureUrl("https://example.com")
                            .build();
    }

    private StudentListDto createStudentDto(int id) {
        return StudentListDto.builder()
                             .id(id)
                             .firstName("John")
                             .lastName("John")
                             .pictureUrl("https://example.com")
                             .build();
    }
}