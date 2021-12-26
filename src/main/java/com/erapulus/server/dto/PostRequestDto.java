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
public class PostRequestDto {

    @NotNull
    @JsonProperty("title")
    private String title;

    @NotNull
    @JsonProperty("content")
    private String content;
}
