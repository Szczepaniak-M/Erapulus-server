package com.erapulus.server.common.web;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseTemplate<T> {

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("payload")
    private T payload;

    @JsonProperty("message")
    private String message;
}
