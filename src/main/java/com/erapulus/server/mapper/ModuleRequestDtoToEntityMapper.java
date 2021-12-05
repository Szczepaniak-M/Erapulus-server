package com.erapulus.server.mapper;

import com.erapulus.server.database.model.ModuleEntity;
import com.erapulus.server.dto.ModuleRequestDto;
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
