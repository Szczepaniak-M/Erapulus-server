package com.erapulus.server.university.dto;

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
public class UniversityRequestDto {

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

}
