package pl.put.erasmusbackend.mapper;

import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.database.model.ModuleEntity;
import pl.put.erasmusbackend.dto.ModuleRequestDto;

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
