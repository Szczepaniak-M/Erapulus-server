package com.erapulus.server.mapper;

import com.erapulus.server.database.model.ProgramEntity;
import com.erapulus.server.dto.program.ProgramRequestDto;
import org.springframework.stereotype.Component;

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
