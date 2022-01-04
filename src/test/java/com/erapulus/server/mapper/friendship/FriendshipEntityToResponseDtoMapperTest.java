package com.erapulus.server.mapper.friendship;

import com.erapulus.server.database.model.FriendshipEntity;
import com.erapulus.server.database.model.FriendshipStatus;
import com.erapulus.server.dto.friendship.FriendshipResponseDto;
import com.erapulus.server.mapper.ValidatorUtils;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FriendshipEntityToResponseDtoMapperTest {

    private static final int ID = 1;
    private static final Integer APPLICATION_USER_ID = 2;
    private static final Integer FRIEND_ID = 3;
    private static final FriendshipStatus STATUS = FriendshipStatus.ACCEPTED;


    @Test
    void from_shouldMapEntityToDto() {
        // given
        FriendshipEntity entity = FriendshipEntity.builder()
                                                  .id(ID)
                                                  .applicationUserId(APPLICATION_USER_ID)
                                                  .friendId(FRIEND_ID)
                                                  .status(STATUS)
                                                  .build();

        // when
        FriendshipResponseDto result = new FriendshipEntityToResponseDtoMapper().from(entity);

        //then
        assertEquals(ID, result.id());
        assertEquals(APPLICATION_USER_ID, result.applicationUserId());
        assertEquals(FRIEND_ID, result.friendId());
        assertEquals(STATUS, result.status());
        assertTrue(ValidatorUtils.createValidator().validate(result).isEmpty());
    }
}