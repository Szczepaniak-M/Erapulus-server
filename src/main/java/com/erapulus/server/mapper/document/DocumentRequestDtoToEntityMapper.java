package com.erapulus.server.mapper.document;

import com.erapulus.server.database.model.DocumentEntity;
import com.erapulus.server.dto.document.DocumentRequestDto;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
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
