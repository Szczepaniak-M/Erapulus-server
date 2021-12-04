package pl.put.erasmusbackend.mapper;

import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.database.model.ModuleEntity;
import pl.put.erasmusbackend.dto.ModuleResponseDto;

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
