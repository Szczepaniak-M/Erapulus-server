package com.erapulus.server.module.mapper;

import com.erapulus.server.common.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.module.database.ModuleEntity;
import com.erapulus.server.module.dto.ModuleRequestDto;
import org.springframework.stereotype.Component;

@Component
public class ModuleRequestDtoToEntityMapper implements RequestDtoToEntityMapper<ModuleRequestDto, ModuleEntity> {
    @Override
    public ModuleEntity from(ModuleRequestDto moduleRequestDto) {
        return ModuleEntity.builder()
                           .name(moduleRequestDto.name())
                           .abbrev(moduleRequestDto.abbrev())
                           .description(moduleRequestDto.description())
                           .build();
    }
}
