package com.erapulus.server.module.mapper;

import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.module.database.ModuleEntity;
import com.erapulus.server.module.dto.ModuleResponseDto;
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
