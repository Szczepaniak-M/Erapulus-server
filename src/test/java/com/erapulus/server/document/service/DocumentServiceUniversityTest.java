package com.erapulus.server.document.service;

import com.erapulus.server.common.service.AzureStorageService;
import com.erapulus.server.document.database.DocumentEntity;
import com.erapulus.server.document.database.DocumentRepository;
import com.erapulus.server.document.dto.DocumentRequestDto;
import com.erapulus.server.document.dto.DocumentResponseDto;
import com.erapulus.server.document.mapper.DocumentEntityToResponseDtoMapper;
import com.erapulus.server.document.mapper.DocumentRequestDtoToEntityMapper;
import com.erapulus.server.module.database.ModuleRepository;
import com.erapulus.server.program.database.ProgramRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceUniversityTest {

    private final static int ID_1 = 1;
    private final static int ID_2 = 2;
    private static final int UNIVERSITY_ID = 3;
    private static final String PATH = "https://azure.com/exmaple.png";

    @Mock
    DocumentRepository documentRepository;

    @Mock
    ProgramRepository programRepository;

    @Mock
    ModuleRepository moduleRepository;

    @Mock
    AzureStorageService azureStorageService;

    DocumentService documentService;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService(documentRepository,
                new DocumentRequestDtoToEntityMapper(),
                new DocumentEntityToResponseDtoMapper(),
                programRepository,
                moduleRepository,
                azureStorageService);
    }

    @Test
    void listDocuments_shouldReturnDocumentListForUniversity() {
        // given
        var document1 = createDocument(ID_1);
        var document2 = createDocument(ID_2);
        when(documentRepository.findAllByFilters(UNIVERSITY_ID, null, null)).thenReturn(Flux.just(document1, document2));

        // when
        Mono<List<DocumentResponseDto>> result = documentService.listDocuments(UNIVERSITY_ID, null, null, null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(documents -> assertEquals(2, documents.size()))
                    .verifyComplete();
    }

    @Test
    void createDocument_shouldCreateDocumentForUniversity() {
        // given
        var filePart = mock(FilePart.class);
        var path = "university/3/document/1/example.png";
        var fullPath = "https://azure.com/university/3/document/1/example.png";
        Map<String, Part> body = new HashMap<>();
        body.put("file", filePart);
        when(filePart.filename()).thenReturn("example.png");
        when(documentRepository.save(any(DocumentEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, DocumentEntity.class).id(ID_1)));
        when(azureStorageService.uploadFile(filePart, path)).thenReturn(Mono.just(fullPath));

        // when
        Mono<DocumentResponseDto> result = documentService.createDocument(UNIVERSITY_ID, null, null, null, body);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(document -> {
                        assertEquals(ID_1, document.id());
                        assertEquals(UNIVERSITY_ID, document.universityId());
                        assertNull(document.facultyId());
                        assertNull(document.programId());
                        assertNull(document.moduleId());
                        assertEquals(fullPath, document.path());
                    })
                    .verifyComplete();
    }

    @Test
    void getDocumentById_shouldReturnDocumentForUniversityWhenFound() {
        // given
        var document = createDocument(ID_1);
        when(documentRepository.findById(ID_1)).thenReturn(Mono.just(document));

        // when
        Mono<DocumentResponseDto> result = documentService.getDocumentById(ID_1, UNIVERSITY_ID, null, null, null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(documentResponseDto -> {
                        assertEquals(ID_1, documentResponseDto.id());
                        assertEquals(UNIVERSITY_ID, documentResponseDto.universityId());
                        assertNull(documentResponseDto.facultyId());
                        assertNull(documentResponseDto.programId());
                        assertNull(documentResponseDto.moduleId());
                    })
                    .verifyComplete();
    }

    @Test
    void getDocumentById_shouldThrowExceptionWhenDocumentForUniversityNotFound() {
        // given
        when(documentRepository.findById(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<DocumentResponseDto> result = documentService.getDocumentById(ID_1, UNIVERSITY_ID, null, null, null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void updateDocument_shouldUpdateDocumentForUniversityWhenFound() {
        // given
        var document = createDocument(ID_1);
        var documentRequestDto = new DocumentRequestDto();
        when(documentRepository.findById(ID_1)).thenReturn(Mono.just(document));
        when(documentRepository.save(any(DocumentEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, DocumentEntity.class).id(ID_1)));

        // when
        Mono<DocumentResponseDto> result = documentService.updateDocument(documentRequestDto, ID_1, UNIVERSITY_ID, null, null, null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(documentResponseDto -> {
                        assertEquals(ID_1, documentResponseDto.id());
                        assertEquals(UNIVERSITY_ID, documentResponseDto.universityId());
                        assertNull(documentResponseDto.facultyId());
                        assertNull(documentResponseDto.programId());
                        assertNull(documentResponseDto.moduleId());
                    })
                    .verifyComplete();
    }

    @Test
    void updateDocument_shouldThrowExceptionWhenDocumentForUniversityNotFound() {
        // given
        var documentRequestDto = new DocumentRequestDto();
        when(documentRepository.findById(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<DocumentResponseDto> result = documentService.updateDocument(documentRequestDto, ID_1, UNIVERSITY_ID, null, null, null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteDocument_shouldDeleteDocumentForUniversityWhenFound() {
        // given
        var document = createDocument(ID_1);
        when(documentRepository.findById(ID_1)).thenReturn(Mono.just(document));
        when(documentRepository.deleteById(ID_1)).thenReturn(Mono.empty());
        when(azureStorageService.deleteFile(document)).thenReturn(Mono.just(true));

        // when
        Mono<Boolean> result = documentService.deleteDocument(ID_1, UNIVERSITY_ID, null, null, null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertTrue)
                    .verifyComplete();
    }

    @Test
    void deleteDocument_shouldThrowExceptionWhenDocumentForUniversityNotFound() {
        // given
        when(documentRepository.findById(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<Boolean> result = documentService.deleteDocument(ID_1, UNIVERSITY_ID, null, null, null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteAllDocumentsByUniversityId() {
        // when
        var document1 = createDocument(ID_1);
        var document2 = createDocument(ID_2);
        when(documentRepository.findAllByFilters(UNIVERSITY_ID, null, null)).thenReturn(Flux.just(document1, document2));
        when(documentRepository.findById(ID_1)).thenReturn(Mono.just(document1));
        when(documentRepository.findById(ID_2)).thenReturn(Mono.just(document2));
        when(documentRepository.deleteById(ID_1)).thenReturn(Mono.empty());
        when(documentRepository.deleteById(ID_2)).thenReturn(Mono.empty());
        when(azureStorageService.deleteFile(document1)).thenReturn(Mono.just(true));
        when(azureStorageService.deleteFile(document2)).thenReturn(Mono.just(true));

        // given
        Flux<Boolean> result = documentService.deleteAllDocumentsByUniversityId(UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectNextCount(2)
                    .verifyComplete();
    }

    private DocumentEntity createDocument(int id) {
        return DocumentEntity.builder()
                             .id(id)
                             .name("name")
                             .path(PATH)
                             .build();
    }
}