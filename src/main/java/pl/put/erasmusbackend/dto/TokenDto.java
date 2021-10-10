package pl.put.erasmusbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenDto {
    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("token")
    private String token;
}
