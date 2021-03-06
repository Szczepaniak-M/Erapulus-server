package com.erapulus.server.building.dto;

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
public class BuildingRequestDto {

    @NotNull
    @JsonProperty("name")
    private String name;

    @NotNull
    @JsonProperty("abbrev")
    private String abbrev;

    @NotNull
    @JsonProperty("latitude")
    private Double latitude;

    @NotNull
    @JsonProperty("longitude")
    private Double longitude;

}
