package com.erapulus.server.module.service;


import com.erapulus.server.common.web.PageablePayload;
import com.erapulus.server.document.service.DocumentService;
import com.erapulus.server.module.database.ModuleEntity;
import com.erapulus.server.module.database.ModuleRepository;
import com.erapulus.server.module.dto.ModuleRequestDto;
import com.erapulus.server.module.dto.ModuleResponseDto;
import com.erapulus.server.module.mapper.ModuleEntityToResponseDtoMapper;
import com.erapulus.server.module.mapper.ModuleRequestDtoToEntityMapper;
import com.erapulus.server.program.database.ProgramRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModuleServiceTest {

    private final static int ID_1 = 1;
    private final static int ID_2 = 2;
    private static final int UNIVERSITY_ID = 3;
    private static final int FACULTY_ID = 4;
    private static final int PROGRAM_ID = 5;

    @Mock
    ModuleRepository moduleRepository;

    @Mock
    ProgramRepository programRepository;

    @Mock
    ModuleService moduleService;

    @Mock
    DocumentService documentService;

    @BeforeEach
    void setUp() {
        moduleService = new ModuleService(moduleRepository,
                programRepository,
                new ModuleRequestDtoToEntityMapper(),
                new ModuleEntityToResponseDtoMapper(),
                documentService);
    }

    @Test
    void listModules_shouldPageableListWhenCorrectInput() {
        // given
        var module1 = createModule(ID_1);
        var module2 = createModule(ID_2);
        var moduleDto1 = createModuleResponseDto(ID_1);
        var moduleDto2 = createModuleResponseDto(ID_2);
        var totalCount = 12;
        String name = "";
        PageRequest pageRequest = PageRequest.of(1, 10);
        when(programRepository.existsByIdAndUniversityIdAndFacultyId(PROGRAM_ID, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(true));
        when(moduleRepository.findByProgramIdAndName(PROGRAM_ID, null, pageRequest.getOffset(), pageRequest.getPageSize()))
                .thenReturn(Flux.just(module1, module2));
        when(moduleRepository.countByProgramIdAndName(PROGRAM_ID, null))
                .thenReturn(Mono.just(totalCount));
        PageablePayload<ModuleResponseDto> expected = new PageablePayload<>(List.of(moduleDto1, moduleDto2), pageRequest, totalCount);

        // when
        Mono<PageablePayload<ModuleResponseDto>> result = moduleService.listModules(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID, name, pageRequest);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(payload -> assertThat(payload)
                            .usingRecursiveComparison()
                            .isEqualTo(expected))
                    .verifyComplete();
    }

    @Test
    void listModules_shouldThrowExceptionWhenProgramNotExists() {
        // given
        String name = "";
        PageRequest pageRequest = PageRequest.of(1, 10);
        when(programRepository.existsByIdAndUniversityIdAndFacultyId(PROGRAM_ID, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(false));
        when(moduleRepository.findByProgramIdAndName(PROGRAM_ID, null, pageRequest.getOffset(), pageRequest.getPageSize()))
                .thenReturn(Flux.error(IllegalStateException::new));
        when(moduleRepository.countByProgramIdAndName(PROGRAM_ID, null))
                .thenReturn(Mono.error(IllegalStateException::new));
        // when
        Mono<PageablePayload<ModuleResponseDto>> result = moduleService.listModules(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID, name, pageRequest);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void createModule_shouldCreateModule() {
        // given
        var moduleRequestDto = new ModuleRequestDto();
        when(programRepository.existsByIdAndUniversityIdAndFacultyId(PROGRAM_ID, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(true));
        when(moduleRepository.save(any(ModuleEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, ModuleEntity.class).id(ID_1)));

        // when
        Mono<ModuleResponseDto> result = moduleService.createModule(moduleRequestDto, UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(module -> {
                        assertEquals(ID_1, module.id());
                        assertEquals(PROGRAM_ID, module.programId());
                    })
                    .verifyComplete();
    }

    @Test
    void createModule_shouldThrowExceptionWhenFacultyNotExists() {
        // given
        var moduleRequestDto = new ModuleRequestDto();
        when(programRepository.existsByIdAndUniversityIdAndFacultyId(PROGRAM_ID, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(false));

        // when
        Mono<ModuleResponseDto> result = moduleService.createModule(moduleRequestDto, UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void getModuleById_shouldReturnModuleWhenFound() {
        // given
        var module = createModule(ID_1);
        when(programRepository.existsByIdAndUniversityIdAndFacultyId(PROGRAM_ID, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(true));
        when(moduleRepository.findByIdAndProgramId(ID_1, PROGRAM_ID)).thenReturn(Mono.just(module));

        // when
        Mono<ModuleResponseDto> result = moduleService.getModuleById(ID_1, UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(moduleResponseDto -> {
                        assertEquals(ID_1, moduleResponseDto.id());
                        assertEquals(PROGRAM_ID, moduleResponseDto.programId());
                    })
                    .verifyComplete();
    }

    @Test
    void getModuleById_shouldThrowExceptionWhenModuleNotFound() {
        // given
        when(programRepository.existsByIdAndUniversityIdAndFacultyId(PROGRAM_ID, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(true));
        when(moduleRepository.findByIdAndProgramId(ID_1, PROGRAM_ID)).thenReturn(Mono.empty());

        // when
        Mono<ModuleResponseDto> result = moduleService.getModuleById(ID_1, UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void getModuleById_shouldThrowExceptionWhenProgramNotFound() {
        // given
        when(programRepository.existsByIdAndUniversityIdAndFacultyId(PROGRAM_ID, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(false));
        when(moduleRepository.findByIdAndProgramId(ID_1, PROGRAM_ID)).thenReturn(Mono.error(IllegalStateException::new));

        // when
        Mono<ModuleResponseDto> result = moduleService.getModuleById(ID_1, UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void updateModule_shouldUpdateModuleWhenFound() {
        // given
        var module = createModule(ID_1);
        var moduleRequestDto = new ModuleRequestDto();
        when(programRepository.existsByIdAndUniversityIdAndFacultyId(PROGRAM_ID, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(true));
        when(moduleRepository.findByIdAndProgramId(ID_1, PROGRAM_ID)).thenReturn(Mono.just(module));
        when(moduleRepository.save(any(ModuleEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, ModuleEntity.class).id(ID_1)));

        // when
        Mono<ModuleResponseDto> result = moduleService.updateModule(moduleRequestDto, ID_1, UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(moduleResponseDto -> {
                        assertEquals(ID_1, moduleResponseDto.id());
                        assertEquals(PROGRAM_ID, moduleResponseDto.programId());
                    })
                    .verifyComplete();
    }

    @Test
    void updateModule_shouldThrowExceptionWhenModuleNotFound() {
        // given
        var moduleRequestDto = new ModuleRequestDto();
        when(programRepository.existsByIdAndUniversityIdAndFacultyId(PROGRAM_ID, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(true));
        when(moduleRepository.findByIdAndProgramId(ID_1, PROGRAM_ID)).thenReturn(Mono.empty());

        // when
        Mono<ModuleResponseDto> result = moduleService.updateModule(moduleRequestDto, ID_1, UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void updateModule_shouldThrowExceptionWhenProgramNotFound() {
        // given
        var moduleRequestDto = new ModuleRequestDto();
        when(programRepository.existsByIdAndUniversityIdAndFacultyId(PROGRAM_ID, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(false));

        // when
        Mono<ModuleResponseDto> result = moduleService.updateModule(moduleRequestDto, ID_1, UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteModule_shouldDeleteModuleWhenFound() {
        // given
        var module = createModule(ID_1);
        when(programRepository.existsByIdAndUniversityIdAndFacultyId(PROGRAM_ID, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(true));
        when(moduleRepository.findByIdAndProgramId(ID_1, PROGRAM_ID)).thenReturn(Mono.just(module));
        when(documentService.deleteAllDocumentsByModuleId(ID_1)).thenReturn(Flux.just(true, true));
        when(moduleRepository.deleteById(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<Boolean> result = moduleService.deleteModule(ID_1, UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertTrue)
                    .verifyComplete();
    }

    @Test
    void deleteModule_shouldThrowExceptionWhenModuleNotFound() {
        // given
        when(programRepository.existsByIdAndUniversityIdAndFacultyId(PROGRAM_ID, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(true));
        when(moduleRepository.findByIdAndProgramId(ID_1, PROGRAM_ID)).thenReturn(Mono.empty());
        when(documentService.deleteAllDocumentsByModuleId(ID_1)).thenReturn(Flux.just(true, true));

        // when
        Mono<Boolean> result = moduleService.deleteModule(ID_1, UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteModule_shouldThrowExceptionWhenProgramNotFound() {
        // given
        when(programRepository.existsByIdAndUniversityIdAndFacultyId(PROGRAM_ID, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(false));
        when(moduleRepository.findByIdAndProgramId(ID_1, PROGRAM_ID)).thenReturn(Mono.error(IllegalStateException::new));

        // when
        Mono<Boolean> result = moduleService.deleteModule(ID_1, UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteAllModuleByProgramId() {
        // when
        var module1 = createModule(ID_1);
        var module2 = createModule(ID_2);
        when(moduleRepository.findAllByProgramId(PROGRAM_ID)).thenReturn(Flux.just(ID_1, ID_2));
        when(documentService.deleteAllDocumentsByModuleId(ID_1)).thenReturn(Flux.just());
        when(documentService.deleteAllDocumentsByModuleId(ID_2)).thenReturn(Flux.just(true, true));
        when(moduleRepository.findByIdAndProgramId(ID_1, PROGRAM_ID)).thenReturn(Mono.just(module1));
        when(moduleRepository.findByIdAndProgramId(ID_2, PROGRAM_ID)).thenReturn(Mono.just(module2));
        when(moduleRepository.deleteById(anyInt())).thenReturn(Mono.empty());

        // given
        Flux<Boolean> result = moduleService.deleteAllModuleByProgramId(PROGRAM_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectNextCount(2)
                    .verifyComplete();
    }

    private ModuleEntity createModule(int id) {
        return ModuleEntity.builder()
                           .id(id)
                           .name("name")
                           .abbrev("abbrev")
                           .programId(PROGRAM_ID)
                           .build();
    }

    private ModuleResponseDto createModuleResponseDto(int id) {
        return ModuleResponseDto.builder()
                                .id(id)
                                .name("name")
                                .abbrev("abbrev")
                                .programId(PROGRAM_ID)
                                .build();
    }
}