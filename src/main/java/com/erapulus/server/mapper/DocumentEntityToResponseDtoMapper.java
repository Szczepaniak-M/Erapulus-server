package com.erapulus.server.mapper;

import com.erapulus.server.database.model.DocumentEntity;
import com.erapulus.server.dto.document.DocumentResponseDto;
import org.springframework.stereotype.Component;

@Component
public class DocumentEntityToResponseDtoMapper implements EntityToResponseDtoMapper<DocumentEntity, DocumentResponseDto> {

    @Override
    public DocumentResponseDto from(DocumentEntity documentEntity) {
        return DocumentResponseDto.builder()
                                  .id(documentEntity.id())
                                  .name(documentEntity.name())
                                  .path(documentEntity.path())
                                  .description(documentEntity.description())
                                  .universityId(documentEntity.universityId())
                                  .programId(documentEntity.programId())
                                  .moduleId(documentEntity.moduleId())
                                  .build();
    }
}
