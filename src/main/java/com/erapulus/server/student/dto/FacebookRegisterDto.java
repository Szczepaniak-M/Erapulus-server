package com.erapulus.server.student.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacebookRegisterDto {

    @NotNull
    @JsonProperty("first_name")
    private String firstName;

    @NotNull
    @JsonProperty("last_name")
    private String lastName;

    @NotNull
    @JsonProperty("email")
    private String email;

    @JsonProperty("picture")
    private Picture picture;

    public String picture() {
        return picture.data.url;
    }

    @Data
    @NoArgsConstructor
    static class Picture {

        @JsonProperty("data")
        private PictureData data;

        @Data
        @NoArgsConstructor
        static class PictureData {
            @JsonProperty("url")
            private String url;
        }
    }
}
