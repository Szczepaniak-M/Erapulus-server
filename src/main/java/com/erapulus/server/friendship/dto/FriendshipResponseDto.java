package com.erapulus.server.friendship.dto;

import com.erapulus.server.friendship.database.FriendshipStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class FriendshipResponseDto {

    @NotNull
    @JsonProperty("id")
    private Integer id;

    @NotNull
    @JsonProperty("applicationUserId")
    private Integer applicationUserId;

    @NotNull
    @JsonProperty("friendId")
    private Integer friendId;

    @NotNull
    @JsonProperty("status")
    private FriendshipStatus status;
}
