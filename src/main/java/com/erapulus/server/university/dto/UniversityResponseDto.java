package com.erapulus.server.university.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class UniversityResponseDto {
    @NotNull
    @JsonProperty("id")
    private Integer id;

    @NotNull
    @JsonProperty("name")
    private String name;

    @NotNull
    @JsonProperty("address")
    private String address;

    @JsonProperty("address2")
    private String address2;

    @NotNull
    @JsonProperty("zipcode")
    private String zipcode;

    @NotNull
    @JsonProperty("city")
    private String city;

    @NotNull
    @JsonProperty("country")
    private String country;

    @JsonProperty("description")
    private String description;

    @JsonProperty("websiteUrl")
    private String websiteUrl;

    @JsonProperty("logoUrl")
    private String logoUrl;
}
