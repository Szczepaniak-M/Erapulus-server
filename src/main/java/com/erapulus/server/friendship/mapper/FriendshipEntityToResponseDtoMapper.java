package com.erapulus.server.friendship.mapper;

import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.friendship.database.FriendshipEntity;
import com.erapulus.server.friendship.dto.FriendshipResponseDto;
import org.springframework.stereotype.Component;

@Component
public class FriendshipEntityToResponseDtoMapper implements EntityToResponseDtoMapper<FriendshipEntity, FriendshipResponseDto> {
    @Override
    public FriendshipResponseDto from(FriendshipEntity friendshipEntity) {
        return FriendshipResponseDto.builder()
                                    .id(friendshipEntity.id())
                                    .applicationUserId(friendshipEntity.applicationUserId())
                                    .friendId(friendshipEntity.friendId())
                                    .status(friendshipEntity.status())
                                    .build();
    }
}
