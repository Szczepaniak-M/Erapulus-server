package com.erapulus.server.module.dto;

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
public class ModuleRequestDto {
    @NotNull
    @JsonProperty("name")
    private String name;

    @NotNull
    @JsonProperty("abbrev")
    private String abbrev;

    @JsonProperty("description")
    private String description;
}
