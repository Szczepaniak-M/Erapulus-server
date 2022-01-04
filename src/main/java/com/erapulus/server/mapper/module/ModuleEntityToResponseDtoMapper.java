package com.erapulus.server.mapper.module;

import com.erapulus.server.database.model.ModuleEntity;
import com.erapulus.server.dto.module.ModuleResponseDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class ModuleEntityToResponseDtoMapper implements EntityToResponseDtoMapper<ModuleEntity, ModuleResponseDto> {
    @Override
    public ModuleResponseDto from(ModuleEntity moduleEntity) {
        return ModuleResponseDto.builder()
                                .id(moduleEntity.id())
                                .name(moduleEntity.name())
                                .abbrev(moduleEntity.abbrev())
                                .description(moduleEntity.description())
                                .programId(moduleEntity.programId())
                                .build();
    }
}
