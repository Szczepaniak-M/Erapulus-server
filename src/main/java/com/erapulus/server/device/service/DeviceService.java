package com.erapulus.server.device.service;

import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.common.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.common.service.CrudGenericService;
import com.erapulus.server.device.database.DeviceEntity;
import com.erapulus.server.device.database.DeviceRepository;
import com.erapulus.server.device.dto.DeviceRequestDto;
import com.erapulus.server.device.dto.DeviceResponseDto;
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
        Supplier<Mono<DeviceEntity>> supplier = () -> deviceRepository.findByIdAndStudentId(deviceId, studentId);
        return updateEntity(requestDto, addParamFromPath, supplier);
    }

    public Mono<Boolean> deleteDevice(Integer deviceId, Integer studentId) {
        Supplier<Mono<DeviceEntity>> supplier = () -> deviceRepository.findByIdAndStudentId(deviceId, studentId);
        return deleteEntity(supplier);
    }

    public Mono<Void> deleteAllDevicesByStudentId(int studentId) {
        return deviceRepository.deleteAllByStudentId(studentId);
    }
}
