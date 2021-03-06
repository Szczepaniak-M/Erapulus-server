package com.erapulus.server.device.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class DeviceResponseDto {
    @NotNull
    @JsonProperty("id")
    private Integer id;

    @NotNull
    @JsonProperty("applicationUserId")
    private Integer applicationUserId;

    @NotNull
    @JsonProperty("deviceId")
    private String deviceId;

    @NotNull
    @JsonProperty("name")
    private String name;
}
