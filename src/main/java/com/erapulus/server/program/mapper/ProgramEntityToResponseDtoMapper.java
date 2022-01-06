package com.erapulus.server.program.mapper;

import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.program.database.ProgramEntity;
import com.erapulus.server.program.dto.ProgramResponseDto;
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
