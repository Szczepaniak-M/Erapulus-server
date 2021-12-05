package com.erapulus.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
public class PostRequestDto {

    @NotNull
    @JsonProperty("title")
    private String title;

    @NotNull
    @JsonProperty("date")
    private LocalDate date;

    @NotNull
    @JsonProperty("content")
    private String content;

    @NotNull
    @JsonProperty("university")
    private Integer universityId;
}
