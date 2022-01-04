package com.erapulus.server.mapper.friendship;

import com.erapulus.server.database.model.FriendshipEntity;
import com.erapulus.server.dto.friendship.FriendshipResponseDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
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
