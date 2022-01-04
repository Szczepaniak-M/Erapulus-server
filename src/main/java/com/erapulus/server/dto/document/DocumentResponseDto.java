package com.erapulus.server.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class DocumentResponseDto {

    @NotNull
    @JsonProperty("id")
    private Integer id;
    
    @NotNull
    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @NotNull
    @JsonProperty("path")
    private String path;

    @JsonProperty("universityId")
    private Integer universityId;

    @JsonProperty("facultyId")
    private Integer facultyId;

    @JsonProperty("programId")
    private Integer programId;

    @JsonProperty("moduleId")
    private Integer moduleId;
}
