package pl.put.erasmusbackend.mapper;

import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.database.model.ProgramEntity;
import pl.put.erasmusbackend.dto.ProgramRequestDto;

@Component
public class ProgramRequestDtoToEntityMapper implements RequestDtoToEntityMapper<ProgramRequestDto, ProgramEntity> {
    @Override
    public ProgramEntity from(ProgramRequestDto programRequestDto) {
        return ProgramEntity.builder()
                            .name(programRequestDto.name())
                            .abbrev(programRequestDto.abbrev())
                            .description(programRequestDto.description())
                            .build();
    }
}
