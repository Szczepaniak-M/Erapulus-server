package com.erapulus.server.faculty.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class FacultyResponseDto {
    @NotNull
    @JsonProperty("id")
    private Integer id;

    @NotNull
    @JsonProperty("name")
    private String name;

    @NotNull
    @JsonProperty("address")
    private String address;

    @NotNull
    @JsonProperty("email")
    private String email;

    @NotNull
    @JsonProperty("universityId")
    private Integer universityId;
}
