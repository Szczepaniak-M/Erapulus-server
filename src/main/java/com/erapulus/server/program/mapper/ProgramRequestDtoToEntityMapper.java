package com.erapulus.server.program.mapper;

import com.erapulus.server.common.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.program.database.ProgramEntity;
import com.erapulus.server.program.dto.ProgramRequestDto;
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
