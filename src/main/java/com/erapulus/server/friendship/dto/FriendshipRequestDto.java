package com.erapulus.server.friendship.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipRequestDto {
    @NotNull
    @JsonProperty("userId")
    private Integer userId;
}
