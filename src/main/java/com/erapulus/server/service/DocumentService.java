package com.erapulus.server.service;

import com.erapulus.server.database.model.DocumentEntity;
import com.erapulus.server.database.repository.DocumentRepository;
import com.erapulus.server.database.repository.ModuleRepository;
import com.erapulus.server.database.repository.ProgramRepository;
import com.erapulus.server.dto.DocumentRequestDto;
import com.erapulus.server.dto.DocumentResponseDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

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
                .map(response -> addParamFromPath(response, universityId, facultyId, programId, moduleId))
                .collectList();
    }

    @Transactional
    public Mono<DocumentResponseDto> createEntity(Integer universityId, Integer facultyId, Integer programId, Integer moduleId, Map<String, Part> body) {
        return validateRequest(universityId, facultyId, programId, moduleId)
                .flatMap(requestDto -> extractFileName(requestDto, body))
                .map(requestDtoToEntityMapper::from)
                .flatMap(documentRepository::save)
                .flatMap(entity -> saveFileToAzure(body, universityId, entity))
                .flatMap(documentRepository::save)
                .map(entityToResponseDtoMapper::from)
                .map(response -> addParamFromPath(response, universityId, facultyId, programId, moduleId));
    }

    public Mono<DocumentResponseDto> getEntityById(Integer documentId, Integer universityId, Integer facultyId, Integer programId, Integer moduleId) {
        Supplier<Mono<DocumentEntity>> supplier = () -> documentRepository.findById(documentId);
        return validateRequest(universityId, facultyId, programId, moduleId)
                .then(getEntityById(supplier))
                .map(response -> addParamFromPath(response, universityId, facultyId, programId, moduleId));
    }

    public Mono<DocumentResponseDto> updateEntity(DocumentRequestDto documentDto, Integer documentId, Integer universityId, Integer facultyId, Integer programId, Integer moduleId) {
        UnaryOperator<DocumentEntity> addParamFromBody = document -> document.id(documentId).name(documentDto.name()).description(documentDto.description());
        BinaryOperator<DocumentEntity> mergeEntity = (oldDocument, newDocument) -> newDocument.path(oldDocument.path());
        return validateRequest(universityId, facultyId, programId, moduleId)
                .flatMap(document -> updateEntity(document, addParamFromBody, mergeEntity))
                .map(response -> addParamFromPath(response, universityId, facultyId, programId, moduleId));
    }

    public Mono<Boolean> deleteEntity(Integer documentId, Integer universityId, Integer facultyId, Integer programId, Integer moduleId) {
        return validateRequest(universityId, facultyId, programId, moduleId)
                .then(documentRepository.findById(documentId))
                .flatMap(document -> deleteEntity(documentId)
                        .then(azureStorageService.deleteFile(document)));
    }

    private Mono<DocumentRequestDto> extractFileName(DocumentRequestDto documentRequestDto, Map<String, Part> body) {
        return getFileName(body)
                .map(name -> documentRequestDto.name(name)
                                               .path(name));
    }

    private Mono<String> getFileName(Map<String, Part> body) {
        return Mono.justOrEmpty(body.get(FILE_QUERY_PARAM))
                   .switchIfEmpty(Mono.error(new IllegalArgumentException("no.file")))
                   .map(FilePart.class::cast)
                   .map(FilePart::filename);
    }

    private Mono<DocumentRequestDto> validateRequest(Integer universityId, Integer facultyId, Integer programId, Integer moduleId) {
        if (moduleId != null) {
            return moduleRepository.existsByIdAndUniversityIdAndFacultyIdAndProgramId(moduleId, universityId, facultyId, programId)
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

    private DocumentResponseDto addParamFromPath(DocumentResponseDto response, Integer universityId, Integer facultyId, Integer programId, Integer moduleId) {
        return response.universityId(universityId)
                       .facultyId(facultyId)
                       .programId(programId)
                       .moduleId(moduleId);
    }
}
