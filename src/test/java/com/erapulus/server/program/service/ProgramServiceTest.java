package com.erapulus.server.program.service;


import com.erapulus.server.common.web.PageablePayload;
import com.erapulus.server.document.service.DocumentService;
import com.erapulus.server.faculty.database.FacultyEntity;
import com.erapulus.server.faculty.database.FacultyRepository;
import com.erapulus.server.module.service.ModuleService;
import com.erapulus.server.program.database.ProgramEntity;
import com.erapulus.server.program.database.ProgramRepository;
import com.erapulus.server.program.dto.ProgramRequestDto;
import com.erapulus.server.program.dto.ProgramResponseDto;
import com.erapulus.server.program.mapper.ProgramEntityToResponseDtoMapper;
import com.erapulus.server.program.mapper.ProgramRequestDtoToEntityMapper;
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
class ProgramServiceTest {

    private final static int ID_1 = 1;
    private final static int ID_2 = 2;
    private static final int UNIVERSITY_ID = 3;
    private static final int FACULTY_ID = 4;

    @Mock
    ProgramRepository programRepository;

    @Mock
    FacultyRepository facultyRepository;

    @Mock
    ModuleService moduleService;

    @Mock
    DocumentService documentService;

    ProgramService programService;

    @BeforeEach
    void setUp() {
        programService = new ProgramService(programRepository,
                facultyRepository,
                new ProgramRequestDtoToEntityMapper(),
                new ProgramEntityToResponseDtoMapper(),
                documentService,
                moduleService);
    }

    @Test
    void listPrograms_shouldPageableListWhenCorrectInput() {
        // given
        var program1 = createProgram(ID_1);
        var program2 = createProgram(ID_2);
        var programDto1 = createProgramResponseDto(ID_1);
        var programDto2 = createProgramResponseDto(ID_2);
        var totalCount = 12;
        String name = "";
        PageRequest pageRequest = PageRequest.of(1, 10);
        when(facultyRepository.findByIdAndUniversityId(FACULTY_ID, UNIVERSITY_ID)).thenReturn(Mono.just(new FacultyEntity()));
        when(programRepository.findByFacultyIdAndName(FACULTY_ID, null, pageRequest.getOffset(), pageRequest.getPageSize()))
                .thenReturn(Flux.just(program1, program2));
        when(programRepository.countByFacultyIdAndName(FACULTY_ID, null))
                .thenReturn(Mono.just(totalCount));
        PageablePayload<ProgramResponseDto> expected = new PageablePayload<>(List.of(programDto1, programDto2), pageRequest, totalCount);

        // when
        Mono<PageablePayload<ProgramResponseDto>> result = programService.listPrograms(UNIVERSITY_ID, FACULTY_ID, name, pageRequest);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(payload -> assertThat(payload)
                            .usingRecursiveComparison()
                            .isEqualTo(expected))
                    .verifyComplete();
    }

    @Test
    void listPrograms_shouldThrowExceptionWhenFacultyNotExists() {
        // given
        String name = "";
        PageRequest pageRequest = PageRequest.of(1, 10);
        when(facultyRepository.findByIdAndUniversityId(FACULTY_ID, UNIVERSITY_ID)).thenReturn(Mono.empty());
        when(programRepository.findByFacultyIdAndName(FACULTY_ID, null, pageRequest.getOffset(), pageRequest.getPageSize())).thenReturn(Flux.error(IllegalStateException::new));
        when(programRepository.countByFacultyIdAndName(FACULTY_ID, null)).thenReturn(Mono.error(IllegalStateException::new));

        // when
        Mono<PageablePayload<ProgramResponseDto>> result = programService.listPrograms(UNIVERSITY_ID, FACULTY_ID, name, pageRequest);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void createProgram_shouldCreateProgram() {
        // given
        var programRequestDto = new ProgramRequestDto();
        when(facultyRepository.findByIdAndUniversityId(FACULTY_ID, UNIVERSITY_ID)).thenReturn(Mono.just(new FacultyEntity()));
        when(programRepository.save(any(ProgramEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, ProgramEntity.class).id(ID_1)));

        // when
        Mono<ProgramResponseDto> result = programService.createProgram(programRequestDto, UNIVERSITY_ID, FACULTY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(program -> {
                        assertEquals(ID_1, program.id());
                        assertEquals(FACULTY_ID, program.facultyId());
                    })
                    .verifyComplete();
    }

    @Test
    void createProgram_shouldThrowExceptionWhenFacultyNotExists() {
        // given
        var programRequestDto = new ProgramRequestDto();
        when(facultyRepository.findByIdAndUniversityId(FACULTY_ID, UNIVERSITY_ID)).thenReturn(Mono.empty());

        // when
        Mono<ProgramResponseDto> result = programService.createProgram(programRequestDto, UNIVERSITY_ID, FACULTY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void getProgramById_shouldReturnProgramWhenFound() {
        // given
        var program = createProgram(ID_1);
        when(programRepository.findByIdAndUniversityIdAndFacultyId(ID_1, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(program));

        // when
        Mono<ProgramResponseDto> result = programService.getProgramById(ID_1, UNIVERSITY_ID, FACULTY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(programResponseDto -> {
                        assertEquals(ID_1, programResponseDto.id());
                        assertEquals(FACULTY_ID, programResponseDto.facultyId());
                    })
                    .verifyComplete();
    }

    @Test
    void getProgramById_shouldThrowExceptionWhenProgramNotFound() {
        // given
        when(programRepository.findByIdAndUniversityIdAndFacultyId(ID_1, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.empty());

        // when
        Mono<ProgramResponseDto> result = programService.getProgramById(ID_1, UNIVERSITY_ID, FACULTY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void updateProgram_shouldUpdateProgramWhenFound() {
        // given
        var program = createProgram(ID_1);
        var programRequestDto = new ProgramRequestDto();
        when(programRepository.findByIdAndUniversityIdAndFacultyId(ID_1, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(program));
        when(programRepository.save(any(ProgramEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, ProgramEntity.class).id(ID_1)));

        // when
        Mono<ProgramResponseDto> result = programService.updateProgram(programRequestDto, ID_1, UNIVERSITY_ID, FACULTY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(programResponseDto -> {
                        assertEquals(ID_1, programResponseDto.id());
                        assertEquals(FACULTY_ID, programResponseDto.facultyId());
                    })
                    .verifyComplete();
    }

    @Test
    void updateProgram_shouldThrowExceptionWhenProgramNotFound() {
        // given
        var programRequestDto = new ProgramRequestDto();
        when(programRepository.findByIdAndUniversityIdAndFacultyId(ID_1, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.empty());

        // when
        Mono<ProgramResponseDto> result = programService.updateProgram(programRequestDto, ID_1, UNIVERSITY_ID, FACULTY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteProgram_shouldDeleteProgramWhenFound() {
        // given
        var program = createProgram(ID_1);
        when(programRepository.findByIdAndUniversityIdAndFacultyId(ID_1, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(program));
        when(moduleService.deleteAllModuleByProgramId(ID_1)).thenReturn(Flux.just(true, true));
        when(documentService.deleteAllDocumentsByProgramId(ID_1)).thenReturn(Flux.just(true, true));
        when(programRepository.deleteById(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<Boolean> result = programService.deleteProgram(ID_1, UNIVERSITY_ID, FACULTY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertTrue)
                    .verifyComplete();
    }

    @Test
    void deleteProgram_shouldThrowExceptionWhenProgramNotFound() {
        // given
        when(programRepository.findByIdAndUniversityIdAndFacultyId(ID_1, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.empty());
        when(moduleService.deleteAllModuleByProgramId(ID_1)).thenReturn(Flux.just(true, true));
        when(documentService.deleteAllDocumentsByProgramId(ID_1)).thenReturn(Flux.just(true, true));

        // when
        Mono<Boolean> result = programService.deleteProgram(ID_1, UNIVERSITY_ID, FACULTY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteAllProgramsByFacultyIdAndUniversityId() {
        // when
        var program1 = createProgram(ID_1);
        var program2 = createProgram(ID_2);
        when(programRepository.findAllByFacultyId(FACULTY_ID)).thenReturn(Flux.just(ID_1, ID_2));
        when(programRepository.findByIdAndUniversityIdAndFacultyId(ID_1, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(program1));
        when(programRepository.findByIdAndUniversityIdAndFacultyId(ID_2, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(program2));
        when(moduleService.deleteAllModuleByProgramId(ID_1)).thenReturn(Flux.just());
        when(moduleService.deleteAllModuleByProgramId(ID_2)).thenReturn(Flux.just(true, true));
        when(documentService.deleteAllDocumentsByProgramId(ID_1)).thenReturn(Flux.just());
        when(documentService.deleteAllDocumentsByProgramId(ID_2)).thenReturn(Flux.just(true, true));
        when(programRepository.deleteById(anyInt())).thenReturn(Mono.empty());

        // given
        Flux<Boolean> result = programService.deleteAllProgramsByFacultyIdAndUniversityId(FACULTY_ID, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectNextCount(2)
                    .verifyComplete();
    }

    private ProgramEntity createProgram(int id) {
        return ProgramEntity.builder()
                            .id(id)
                            .name("name")
                            .abbrev("abbrev")
                            .facultyId(FACULTY_ID)
                            .build();
    }

    private ProgramResponseDto createProgramResponseDto(int id) {
        return ProgramResponseDto.builder()
                                 .id(id)
                                 .name("name")
                                 .abbrev("abbrev")
                                 .facultyId(FACULTY_ID)
                                 .build();
    }
}