package com.erapulus.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipDecisionDto {

    @NotNull
    @JsonProperty("accept")
    private Boolean isAccepted;

}

