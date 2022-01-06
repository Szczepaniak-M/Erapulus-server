package com.erapulus.server.document.mapper;

import com.erapulus.server.common.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.document.database.DocumentEntity;
import com.erapulus.server.document.dto.DocumentRequestDto;
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
