package com.erapulus.server.service;

import com.erapulus.server.database.model.DocumentEntity;
import com.erapulus.server.database.repository.DocumentRepository;
import com.erapulus.server.database.repository.ModuleRepository;
import com.erapulus.server.database.repository.ProgramRepository;
import com.erapulus.server.dto.DocumentRequestDto;
import com.erapulus.server.dto.DocumentResponseDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.erapulus.server.web.common.CommonRequestVariable.FILE_QUERY_PARAM;

@Service
public class DocumentService extends CrudGenericService<DocumentEntity, DocumentRequestDto, DocumentResponseDto> {

    private final DocumentRepository documentRepository;
    private final ProgramRepository programRepository;
    private final ModuleRepository moduleRepository;
    private final AzureStorageService azureStorageService;

    public DocumentService(DocumentRepository documentRepository,
                           RequestDtoToEntityMapper<DocumentRequestDto, DocumentEntity> requestDtoToEntityMapper,
                           EntityToResponseDtoMapper<DocumentEntity, DocumentResponseDto> entityToResponseDtoMapper,
                           ProgramRepository programRepository,
                           ModuleRepository moduleRepository,
                           AzureStorageService azureStorageService) {
        super(documentRepository, requestDtoToEntityMapper, entityToResponseDtoMapper, "document");
        this.documentRepository = documentRepository;
        this.programRepository = programRepository;
        this.moduleRepository = moduleRepository;
        this.azureStorageService = azureStorageService;
    }

    public Mono<List<DocumentResponseDto>> listEntities(Integer universityId, Integer facultyId, Integer programId, Integer moduleId) {
        return validateRequest(universityId, facultyId, programId, moduleId)
                .flatMapMany(requestDto -> documentRepository.findAllByFilters(requestDto.universityId(), requestDto.programId(), requestDto.moduleId()))
                .map(entityToResponseDtoMapper::from)
                .collectList();
    }

    public Mono<DocumentResponseDto> createEntity(Integer universityId, Integer facultyId, Integer programId, Integer moduleId, Map<String, Part> body) {
        return validateRequest(universityId, facultyId, programId, moduleId)
                .flatMap(requestDto -> transformMapToDto(requestDto, body))
                .map(requestDtoToEntityMapper::from)
                .flatMap(documentRepository::save)
                .flatMap(entity -> saveFileToAzure(body, universityId, entity))
                .flatMap(documentRepository::save)
                .map(entityToResponseDtoMapper::from);
    }

    private Mono<DocumentRequestDto> transformMapToDto(DocumentRequestDto documentRequestDto, Map<String, Part> body) {
        Mono<String> fileName = getFileName(body);
        Mono<String> description = getDescription(body);
        return Mono.zip(fileName, description,
                (name, desc) -> documentRequestDto.name(name)
                                                  .description(desc)
                                                  .path(name));
    }

    private Mono<String> getFileName(Map<String, Part> body) {
        return Mono.justOrEmpty(body.get(FILE_QUERY_PARAM))
                   .switchIfEmpty(Mono.error(new IllegalArgumentException("no.file")))
                   .map(FilePart.class::cast)
                   .map(FilePart::filename);
    }

    private Mono<String> getDescription(Map<String, Part> body) {
        return Mono.justOrEmpty(body.get("description"))
                   .flatMap(part -> DataBufferUtils.join(part.content()))
                   .flatMap(this::toString)
                   .switchIfEmpty(Mono.just(""));
    }

    private Mono<String> toString(DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        return Mono.just(new String(bytes, StandardCharsets.UTF_8));
    }

    private Mono<DocumentRequestDto> validateRequest(Integer universityId, Integer facultyId, Integer programId, Integer moduleId) {
        if (moduleId != null) {
            return moduleRepository.existsByIdAndUniversityIdAndFacultyIdAndProgramId(programId, universityId, facultyId, moduleId)
                                   .flatMap(exists -> exists ? Mono.just(DocumentRequestDto.builder().programId(programId).build())
                                                             : Mono.error(new NoSuchElementException("module")));
        } else if (programId != null) {
            return programRepository.existsByIdAndUniversityIdAndFacultyId(programId, universityId, facultyId)
                                    .flatMap(exists -> exists ? Mono.just(DocumentRequestDto.builder().programId(programId).build())
                                                              : Mono.error(new NoSuchElementException("program")));
        } else {
            return Mono.just(DocumentRequestDto.builder().universityId(universityId).build());
        }
    }

    private Mono<DocumentEntity> saveFileToAzure(Map<String, Part> body, Integer universityId, DocumentEntity documentEntity) {
        FilePart file = (FilePart) body.get(FILE_QUERY_PARAM);
        String filePath = "university/%d/document/%d/%s".formatted(universityId, documentEntity.id(), file.filename());
        return azureStorageService.uploadFile(file, filePath)
                                  .map(documentEntity::path);
    }
}
