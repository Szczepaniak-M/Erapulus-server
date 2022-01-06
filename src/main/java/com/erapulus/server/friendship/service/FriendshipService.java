package com.erapulus.server.friendship.service;

import com.erapulus.server.common.web.PageablePayload;
import com.erapulus.server.friendship.database.FriendshipEntity;
import com.erapulus.server.friendship.database.FriendshipRepository;
import com.erapulus.server.friendship.database.FriendshipStatus;
import com.erapulus.server.friendship.dto.FriendshipDecisionDto;
import com.erapulus.server.friendship.dto.FriendshipRequestDto;
import com.erapulus.server.friendship.dto.FriendshipResponseDto;
import com.erapulus.server.friendship.mapper.FriendshipEntityToResponseDtoMapper;
import com.erapulus.server.student.database.StudentRepository;
import com.erapulus.server.student.dto.StudentListDto;
import com.erapulus.server.student.mapper.StudentEntityToListDtoMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

import static com.erapulus.server.common.service.QueryParamParser.parseString;

@Service
@Validated
@AllArgsConstructor
public class FriendshipService {

    private static final String REQUEST = "request";
    private static final String FRIEND = "friend";
    private final FriendshipRepository friendshipRepository;
    private final StudentRepository studentRepository;
    private final PushNotificationService pushNotificationService;
    private final FriendshipEntityToResponseDtoMapper friendshipEntityToResponseDtoMapper;

    public Mono<PageablePayload<StudentListDto>> listFriends(int studentId, String name, PageRequest pageRequest) {
        String nameParsed = parseString(name);
        return studentRepository.findByIdAndType(studentId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException("student")))
                                .thenMany(friendshipRepository.findFriendsByIdAndFilters(studentId, nameParsed, pageRequest.getOffset(), pageRequest.getPageSize()))
                                .map(StudentEntityToListDtoMapper::from)
                                .collectList()
                                .zipWith(friendshipRepository.countFriendsByIdAndFilters(studentId, nameParsed))
                                .map(dtoListAndTotalCount -> new PageablePayload<>(dtoListAndTotalCount.getT1(), pageRequest, dtoListAndTotalCount.getT2()));
    }

    public Mono<List<StudentListDto>> listFriendRequests(int studentId) {
        return friendshipRepository.findFriendRequestsById(studentId)
                                   .map(StudentEntityToListDtoMapper::from)
                                   .collectList();
    }

    public Mono<FriendshipResponseDto> addFriendRequest(@Valid FriendshipRequestDto friendId, int studentId) {
        return studentRepository.findByIdAndType(studentId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException(FRIEND)))
                                .then(friendshipRepository.findByUserIdAndFriendId(studentId, friendId.userId()))
                                .flatMap(f -> Mono.error(new IllegalArgumentException(REQUEST)))
                                .thenReturn(createFriendshipEntity(friendId.userId(), studentId, FriendshipStatus.REQUESTED))
                                .flatMap(friendshipRepository::save)
                                .map(friendshipEntityToResponseDtoMapper::from)
                                .flatMap(response -> pushNotificationService.sendPushNotification(response)
                                                                            .thenReturn(response));
    }

    public Mono<FriendshipResponseDto> handleFriendshipRequest(@Valid FriendshipDecisionDto decisionDto, int studentId, int friendId) {
        Mono<FriendshipEntity> response;
        if (decisionDto.accept()) {
            response = friendshipRepository.findByUserIdAndFriendId(studentId, friendId)
                                           .switchIfEmpty(Mono.error(new NoSuchElementException(REQUEST)))
                                           .map(friendship -> friendship.status(FriendshipStatus.ACCEPTED))
                                           .flatMap(friendshipRepository::save)
                                           .map(FriendshipEntity::reverse)
                                           .flatMap(friendshipRepository::save);
        } else {
            response = friendshipRepository.deleteByUserIdAndFriendId(studentId, friendId)
                                           .flatMap(deleteCount -> deleteCount == 0 ? Mono.error(new NoSuchElementException(REQUEST)) : Mono.empty())
                                           .thenReturn(createFriendshipEntity(studentId, friendId, FriendshipStatus.DECLINED));
        }
        return response.map(friendshipEntityToResponseDtoMapper::from);
    }

    @Transactional
    public Mono<Boolean> deleteFriend(int studentId, int friendId) {
        return friendshipRepository.deleteByUserIdAndFriendId(studentId, friendId)
                                   .flatMap(count -> count == 2 ? Mono.just(true) : Mono.error(new NoSuchElementException(FRIEND)));
    }

    public Mono<Void> deleteAllFriendsByStudentId(int studentId) {
        return friendshipRepository.deleteAllByStudentId(studentId);
    }

    private FriendshipEntity createFriendshipEntity(int studentId, int friendId, FriendshipStatus status) {
        return FriendshipEntity.builder()
                               .status(status)
                               .applicationUserId(studentId)
                               .friendId(friendId)
                               .build();
    }
}
