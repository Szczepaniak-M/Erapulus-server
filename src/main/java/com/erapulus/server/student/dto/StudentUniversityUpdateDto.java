package com.erapulus.server.student.dto;

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
public class StudentUniversityUpdateDto {

    @NotNull
    @JsonProperty("universityId")
    private Integer universityId;
}
