package com.erapulus.server.document.mapper;

import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.document.database.DocumentEntity;
import com.erapulus.server.document.dto.DocumentResponseDto;
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
