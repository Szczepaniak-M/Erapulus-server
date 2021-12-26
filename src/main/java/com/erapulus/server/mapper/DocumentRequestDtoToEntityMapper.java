package com.erapulus.server.mapper;

import com.erapulus.server.database.model.DocumentEntity;
import com.erapulus.server.dto.DocumentRequestDto;
import org.springframework.stereotype.Component;

@Component
public class DocumentRequestDtoToEntityMapper implements RequestDtoToEntityMapper<DocumentRequestDto, DocumentEntity> {

    @Override
    public DocumentEntity from(DocumentRequestDto documentRequestDto) {
        return DocumentEntity.builder()
                             .name(documentRequestDto.name())
                             .path(documentRequestDto.path())
                             .description(documentRequestDto.description())
                             .universityId(documentRequestDto.universityId())
                             .programId(documentRequestDto.programId())
                             .moduleId(documentRequestDto.moduleId())
                             .build();
    }
}
