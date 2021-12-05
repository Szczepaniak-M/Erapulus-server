package com.erapulus.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class StudentDto {

    @NotNull
    @JsonProperty("id")
    private Integer id;

    @NotNull
    @JsonProperty("firstName")
    private String firstName;

    @NotNull
    @JsonProperty("lastName")
    private String lastName;

    @NotNull
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
