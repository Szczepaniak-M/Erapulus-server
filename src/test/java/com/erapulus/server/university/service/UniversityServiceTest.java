package com.erapulus.server.university.service;

import com.erapulus.server.building.service.BuildingService;
import com.erapulus.server.common.service.AzureStorageService;
import com.erapulus.server.document.service.DocumentService;
import com.erapulus.server.employee.service.EmployeeService;
import com.erapulus.server.faculty.service.FacultyService;
import com.erapulus.server.post.service.PostService;
import com.erapulus.server.university.database.UniversityEntity;
import com.erapulus.server.university.database.UniversityRepository;
import com.erapulus.server.university.dto.UniversityListDto;
import com.erapulus.server.university.dto.UniversityRequestDto;
import com.erapulus.server.university.dto.UniversityResponseDto;
import com.erapulus.server.university.mapper.UniversityEntityToListDtoMapper;
import com.erapulus.server.university.mapper.UniversityEntityToResponseDtoMapper;
import com.erapulus.server.university.mapper.UniversityRequestDtoToEntityMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniversityServiceTest {


    private final static int ID_1 = 1;
    private final static int ID_2 = 2;
    private final static String LOGO_URL = "https://example.com";

    @Mock
    UniversityRepository universityRepository;

    @Mock
    AzureStorageService azureStorageService;

    @Mock
    FacultyService facultyService;

    @Mock
    DocumentService documentService;

    @Mock
    PostService postService;

    @Mock
    EmployeeService employeeService;

    @Mock
    BuildingService buildingService;

    UniversityService universityService;

    @BeforeEach
    void setUp() {
        universityService = new UniversityService(universityRepository,
                new UniversityRequestDtoToEntityMapper(),
                new UniversityEntityToResponseDtoMapper(),
                new UniversityEntityToListDtoMapper(),
                azureStorageService,
                facultyService,
                documentService,
                postService,
                employeeService,
                buildingService);
    }

    @Test
    void listUniversities_shouldReturnUniversityList() {
        // given
        var university1 = createUniversity(ID_1);
        var university2 = createUniversity(ID_2);
        when(universityRepository.findAllUniversities()).thenReturn(Flux.just(university1, university2));

        // when
        Mono<List<UniversityListDto>> result = universityService.listUniversities();

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(universities -> assertEquals(2, universities.size()))
                    .verifyComplete();
    }

    @Test
    void createUniversity_shouldCreateUniversity() {
        // given
        var universityRequestDto = new UniversityRequestDto();
        when(universityRepository.save(any(UniversityEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, UniversityEntity.class).id(ID_1)));

        // when
        Mono<UniversityResponseDto> result = universityService.createUniversity(universityRequestDto);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(university -> assertEquals(ID_1, university.id()))
                    .verifyComplete();
    }

    @Test
    void getUniversityById_shouldReturnUniversityWhenFound() {
        // given
        var university = createUniversity(ID_1);
        when(universityRepository.findById(ID_1)).thenReturn(Mono.just(university));

        // when
        Mono<UniversityResponseDto> result = universityService.getUniversityById(ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(universityResponseDto -> assertEquals(ID_1, universityResponseDto.id()))
                    .verifyComplete();
    }

    @Test
    void getUniversityById_shouldThrowExceptionWhenUniversityNotFound() {
        // given
        when(universityRepository.findById(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<UniversityResponseDto> result = universityService.getUniversityById(ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void updateUniversity_shouldUpdateUniversityWhenFound() {
        // given
        var university = createUniversity(ID_1);
        var universityRequestDto = new UniversityRequestDto();
        when(universityRepository.findById(ID_1)).thenReturn(Mono.just(university));
        when(universityRepository.save(any(UniversityEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, UniversityEntity.class).id(ID_1)));

        // when
        Mono<UniversityResponseDto> result = universityService.updateUniversity(universityRequestDto, ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(universityResponseDto -> {
                        assertEquals(ID_1, universityResponseDto.id());
                        assertEquals(LOGO_URL, universityResponseDto.logoUrl());
                    })
                    .verifyComplete();
    }

    @Test
    void updateUniversity_shouldThrowExceptionWhenUniversityNotFound() {
        // given
        var universityRequestDto = new UniversityRequestDto();
        when(universityRepository.findById(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<UniversityResponseDto> result = universityService.updateUniversity(universityRequestDto, ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteUniversity_shouldDeleteUniversityWhenFound() {
        // given
        var university = createUniversity(ID_1);
        when(universityRepository.findById(ID_1)).thenReturn(Mono.just(university));
        when(facultyService.deleteAllFacultiesByUniversityId(ID_1)).thenReturn(Flux.just(true, true));
        when(documentService.deleteAllDocumentsByUniversityId(ID_1)).thenReturn(Flux.just(true, true));
        when(buildingService.deleteAllBuildingsByUniversityId(ID_1)).thenReturn(Mono.empty());
        when(postService.deleteAllPostsByUniversityId(ID_1)).thenReturn(Mono.empty());
        when(employeeService.deleteAllEmployeesByUniversityId(ID_1)).thenReturn(Mono.empty());
        when(universityRepository.deleteById(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<Boolean> result = universityService.deleteUniversity(ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertTrue)
                    .verifyComplete();
    }

    @Test
    void deleteUniversity_shouldThrowExceptionWhenUniversityNotFound() {
        // given
        when(universityRepository.findById(ID_1)).thenReturn(Mono.empty());
        when(facultyService.deleteAllFacultiesByUniversityId(ID_1)).thenReturn(Flux.empty());
        when(documentService.deleteAllDocumentsByUniversityId(ID_1)).thenReturn(Flux.empty());
        when(buildingService.deleteAllBuildingsByUniversityId(ID_1)).thenReturn(Mono.empty());
        when(postService.deleteAllPostsByUniversityId(ID_1)).thenReturn(Mono.empty());
        when(employeeService.deleteAllEmployeesByUniversityId(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<Boolean> result = universityService.deleteUniversity(ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteUniversity_shouldThrowExceptionWhenExceptionOccurInOtherService() {
        // given
        var university = createUniversity(ID_1);
        when(universityRepository.findById(ID_1)).thenReturn(Mono.just(university));
        when(facultyService.deleteAllFacultiesByUniversityId(ID_1)).thenReturn(Flux.error(RuntimeException::new));
        when(documentService.deleteAllDocumentsByUniversityId(ID_1)).thenReturn(Flux.empty());
        when(buildingService.deleteAllBuildingsByUniversityId(ID_1)).thenReturn(Mono.empty());
        when(postService.deleteAllPostsByUniversityId(ID_1)).thenReturn(Mono.empty());
        when(employeeService.deleteAllEmployeesByUniversityId(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<Boolean> result = universityService.deleteUniversity(ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(RuntimeException.class)
                    .verify();
    }

    @Test
    void updateUniversityLogo_shouldUpdateStudentPhoto() {
        // given
        var university = createUniversity(ID_1);
        var filePart = mock(FilePart.class);
        var path = "university/1/logo/example.png";
        var fullPath = "https://azure.com/user/1/photo/example.png";
        when(filePart.filename()).thenReturn("example.png");
        when(universityRepository.findById(ID_1)).thenReturn(Mono.just(university));
        when(azureStorageService.uploadFile(filePart, path)).thenReturn(Mono.just(fullPath));
        when(universityRepository.save(university.logoUrl(path)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, UniversityEntity.class)));

        // when
        Mono<UniversityResponseDto> result = universityService.updateUniversityLogo(ID_1, filePart);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(universityResponseDto -> {
                        assertEquals(ID_1, universityResponseDto.id());
                        assertEquals(fullPath, universityResponseDto.logoUrl());
                    })
                    .verifyComplete();
    }

    @Test
    void updateUniversityLogo_shouldThrowExceptionWhenUserNotFound() {
        // given
        var filePart = mock(FilePart.class);
        when(filePart.filename()).thenReturn("example.png");
        when(universityRepository.findById(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<UniversityResponseDto> result = universityService.updateUniversityLogo(ID_1, filePart);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void updateUniversityLogo_shouldThrowExceptionWhenUploadFail() {
        // given
        var university = createUniversity(ID_1);
        var filePart = mock(FilePart.class);
        var path = "university/1/logo/example.png";
        when(filePart.filename()).thenReturn("example.png");
        when(universityRepository.findById(ID_1)).thenReturn(Mono.just(university));
        when(azureStorageService.uploadFile(filePart, path)).thenReturn(Mono.error(new RuntimeException()));

        // when
        Mono<UniversityResponseDto> result = universityService.updateUniversityLogo(ID_1, filePart);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(RuntimeException.class)
                    .verify();
    }

    private UniversityEntity createUniversity(int id) {
        return UniversityEntity.builder()
                               .id(id)
                               .name("name")
                               .logoUrl(LOGO_URL)
                               .build();
    }
}