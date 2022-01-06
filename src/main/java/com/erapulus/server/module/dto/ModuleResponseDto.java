package com.erapulus.server.module.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ModuleResponseDto {
    @NotNull
    @JsonProperty("id")
    private Integer id;

    @NotNull
    @JsonProperty("name")
    private String name;

    @NotNull
    @JsonProperty("abbrev")
    private String abbrev;

    @JsonProperty("description")
    private String description;

    @NotNull
    @JsonProperty("programId")
    private Integer programId;
}
