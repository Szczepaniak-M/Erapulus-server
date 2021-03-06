package com.erapulus.server.device.dto;

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
public class DeviceRequestDto {
    @NotNull
    @JsonProperty("deviceId")
    private String deviceId;

    @NotNull
    @JsonProperty("name")
    private String name;
}
