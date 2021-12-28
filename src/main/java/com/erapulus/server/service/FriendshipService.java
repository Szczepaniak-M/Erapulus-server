package com.erapulus.server.service;

import com.erapulus.server.database.model.FriendshipEntity;
import com.erapulus.server.database.model.FriendshipStatus;
import com.erapulus.server.database.repository.FriendshipRepository;
import com.erapulus.server.database.repository.StudentRepository;
import com.erapulus.server.dto.FriendshipDecisionDto;
import com.erapulus.server.dto.FriendshipRequestDto;
import com.erapulus.server.dto.FriendshipResponseDto;
import com.erapulus.server.dto.StudentListDto;
import com.erapulus.server.mapper.FriendshipEntityToResponseDtoMapper;
import com.erapulus.server.mapper.StudentEntityToListDtoMapper;
import com.erapulus.server.web.common.PageablePayload;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

import static com.erapulus.server.service.QueryParamParser.parseString;

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
        if (decisionDto.isAccepted()) {
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

    private FriendshipEntity createFriendshipEntity(int studentId, int friendId, FriendshipStatus status) {
        return FriendshipEntity.builder()
                               .status(status)
                               .applicationUserId(studentId)
                               .friendId(friendId)
                               .build();
    }

}
