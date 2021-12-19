package com.erapulus.server.dto;

import com.erapulus.server.database.model.UserType;
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
public class ApplicationUserDto {

    @NotNull
    @JsonProperty("id")
    private Integer id;

    @NotNull
    @JsonProperty("type")
    private UserType type;

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

}