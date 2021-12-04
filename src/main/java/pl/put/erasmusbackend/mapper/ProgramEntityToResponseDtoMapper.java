package pl.put.erasmusbackend.mapper;

import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.database.model.ProgramEntity;
import pl.put.erasmusbackend.dto.ProgramResponseDto;

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
