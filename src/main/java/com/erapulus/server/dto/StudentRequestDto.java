package com.erapulus.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDto {

    @NotNull
    @JsonProperty("firstName")
    private String firstName;

    @NotNull
    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("university")
    private Integer universityId;

    @NotNull
    @JsonProperty("email")
    private String email;

    @JsonProperty("facebookUrl")
    private String facebookUrl;

    @JsonProperty("whatsUpUrl")
    private String whatsUpUrl;

    @JsonProperty("instagramUsername")
    private String instagramUsername;
}
