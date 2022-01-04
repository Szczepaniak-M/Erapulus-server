package com.erapulus.server.mapper;

import com.erapulus.server.database.model.ProgramEntity;
import com.erapulus.server.dto.program.ProgramResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ProgramEntityToResponseDtoMapper implements EntityToResponseDtoMapper<ProgramEntity, ProgramResponseDto> {
    @Override
    public ProgramResponseDto from(ProgramEntity programEntity) {
        return ProgramResponseDto.builder()
                                 .id(programEntity.id())
                                 .name(programEntity.name())
                                 .abbrev(programEntity.abbrev())
                                 .description(programEntity.description())
                                 .facultyId(programEntity.facultyId())
                                 .build();
    }
}
