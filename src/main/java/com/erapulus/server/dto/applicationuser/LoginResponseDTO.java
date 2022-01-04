package com.erapulus.server.dto.applicationuser;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {
    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("universityId")
    private Integer universityId;

    @JsonProperty("token")
    private String token;
}
