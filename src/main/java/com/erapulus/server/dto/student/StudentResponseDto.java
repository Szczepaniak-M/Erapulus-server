package com.erapulus.server.dto.student;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class StudentResponseDto {

    @NotNull
    @JsonProperty("id")
    private Integer id;

    @NotNull
    @JsonProperty("firstName")
    private String firstName;

    @NotNull
    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("universityId")
    private Integer universityId;

    @NotNull
    @JsonProperty("email")
    private String email;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("pictureUrl")
    private String pictureUrl;

    @JsonProperty("facebookUrl")
    private String facebookUrl;

    @JsonProperty("whatsUpUrl")
    private String whatsUpUrl;

    @JsonProperty("instagramUsername")
    private String instagramUsername;
}
