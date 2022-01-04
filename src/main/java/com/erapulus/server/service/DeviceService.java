package com.erapulus.server.service;

import com.erapulus.server.database.model.DeviceEntity;
import com.erapulus.server.database.repository.DeviceRepository;
import com.erapulus.server.dto.device.DeviceRequestDto;
import com.erapulus.server.dto.device.DeviceResponseDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@Component
public class DeviceService extends CrudGenericService<DeviceEntity, DeviceRequestDto, DeviceResponseDto> {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository,
                         RequestDtoToEntityMapper<DeviceRequestDto, DeviceEntity> requestDtoToEntityMapper,
                         EntityToResponseDtoMapper<DeviceEntity, DeviceResponseDto> entityToResponseDtoMapper) {
        super(deviceRepository, requestDtoToEntityMapper, entityToResponseDtoMapper, "device");
        this.deviceRepository = deviceRepository;
    }

    public Mono<List<DeviceResponseDto>> listDevices(Integer studentId) {
        return deviceRepository.findAllByStudentId(studentId)
                               .map(entityToResponseDtoMapper::from)
                               .collectList();
    }

    public Mono<DeviceResponseDto> createDevice(@Valid DeviceRequestDto requestDto, int studentId) {
        UnaryOperator<DeviceEntity> addParamFromPath = device -> device.applicationUserId(studentId);
        return createEntity(requestDto, addParamFromPath);
    }

    public Mono<DeviceResponseDto> getDeviceById(int deviceId, int studentId) {
        Supplier<Mono<DeviceEntity>> supplier = () -> deviceRepository.findByIdAndStudentId(deviceId, studentId);
        return getEntityById(supplier);
    }

    public Mono<DeviceResponseDto> updateDevice(@Valid DeviceRequestDto requestDto, int deviceId, int studentId) {
        UnaryOperator<DeviceEntity> addParamFromPath = device -> device.id(deviceId).applicationUserId(studentId);
        return updateEntity(requestDto, addParamFromPath);
    }

    public Mono<Void> deleteAllDevicesByStudentId(int studentId) {
        return deviceRepository.deleteAllByStudentId(studentId);
    }
}
