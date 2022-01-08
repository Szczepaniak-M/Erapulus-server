package com.erapulus.server.faculty.service;


import com.erapulus.server.common.web.PageablePayload;
import com.erapulus.server.faculty.database.FacultyEntity;
import com.erapulus.server.faculty.database.FacultyRepository;
import com.erapulus.server.faculty.dto.FacultyRequestDto;
import com.erapulus.server.faculty.dto.FacultyResponseDto;
import com.erapulus.server.faculty.mapper.FacultyEntityToResponseDtoMapper;
import com.erapulus.server.faculty.mapper.FacultyRequestDtoToEntityMapper;
import com.erapulus.server.program.service.ProgramService;
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
class FacultyServiceTest {

    public static final int UNIVERSITY_ID = 1;
    private final static int ID_1 = 1;
    private final static int ID_2 = 2;

    @Mock
    FacultyRepository facultyRepository;

    @Mock
    ProgramService programService;

    FacultyService facultyService;

    @BeforeEach
    void setUp() {
        facultyService = new FacultyService(facultyRepository,
                new FacultyRequestDtoToEntityMapper(),
                new FacultyEntityToResponseDtoMapper(),
                programService);
    }

    @Test
    void listFaculties_shouldPageableListWhenCorrectInput() {
        // given
        var faculty1 = createFaculty(ID_1);
        var faculty2 = createFaculty(ID_2);
        var facultyDto1 = createFacultyResponseDto(ID_1);
        var facultyDto2 = createFacultyResponseDto(ID_2);
        var totalCount = 12;
        String name = "";
        PageRequest pageRequest = PageRequest.of(1, 10);
        when(facultyRepository.findByUniversityIdAndName(UNIVERSITY_ID, null, pageRequest.getOffset(), pageRequest.getPageSize()))
                .thenReturn(Flux.just(faculty1, faculty2));
        when(facultyRepository.countByUniversityIdAndName(UNIVERSITY_ID, null))
                .thenReturn(Mono.just(totalCount));
        PageablePayload<FacultyResponseDto> expected = new PageablePayload<>(List.of(facultyDto1, facultyDto2), pageRequest, totalCount);

        // when
        Mono<PageablePayload<FacultyResponseDto>> result = facultyService.listFaculties(UNIVERSITY_ID, name, pageRequest);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(payload -> assertThat(payload)
                            .usingRecursiveComparison()
                            .isEqualTo(expected))
                    .verifyComplete();
    }

    @Test
    void createFaculty_shouldCreateFaculty() {
        // given
        var facultyRequestDto = new FacultyRequestDto();
        when(facultyRepository.save(any(FacultyEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, FacultyEntity.class).id(ID_1)));

        // when
        Mono<FacultyResponseDto> result = facultyService.createFaculty(facultyRequestDto, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(faculty -> {
                        assertEquals(ID_1, faculty.id());
                        assertEquals(UNIVERSITY_ID, faculty.universityId());
                    })
                    .verifyComplete();
    }

    @Test
    void getFacultyById_shouldReturnFacultyWhenFound() {
        // given
        var faculty = createFaculty(ID_1);
        when(facultyRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.just(faculty));

        // when
        Mono<FacultyResponseDto> result = facultyService.getFacultyById(ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(facultyResponseDto -> {
                        assertEquals(ID_1, facultyResponseDto.id());
                        assertEquals(UNIVERSITY_ID, facultyResponseDto.universityId());
                    })
                    .verifyComplete();
    }

    @Test
    void getFacultyById_shouldThrowExceptionWhenFacultyNotFound() {
        // given
        when(facultyRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.empty());

        // when
        Mono<FacultyResponseDto> result = facultyService.getFacultyById(ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void updateFaculty_shouldUpdateFacultyWhenFound() {
        // given
        var faculty = createFaculty(ID_1);
        var facultyRequestDto = new FacultyRequestDto();
        when(facultyRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.just(faculty));
        when(facultyRepository.save(any(FacultyEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, FacultyEntity.class).id(ID_1)));

        // when
        Mono<FacultyResponseDto> result = facultyService.updateFaculty(facultyRequestDto, ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(facultyResponseDto -> {
                        assertEquals(ID_1, facultyResponseDto.id());
                        assertEquals(UNIVERSITY_ID, facultyResponseDto.universityId());
                    })
                    .verifyComplete();
    }

    @Test
    void updateFaculty_shouldThrowExceptionWhenFacultyNotFound() {
        // given
        var facultyRequestDto = new FacultyRequestDto();
        when(facultyRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.empty());

        // when
        Mono<FacultyResponseDto> result = facultyService.updateFaculty(facultyRequestDto, ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteFaculty_shouldDeleteFacultyWhenFound() {
        // given
        var faculty = createFaculty(ID_1);
        when(facultyRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.just(faculty));
        when(programService.deleteAllProgramsByFacultyIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Flux.just(true, true));
        when(facultyRepository.deleteById(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<Boolean> result = facultyService.deleteFaculty(ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertTrue)
                    .verifyComplete();
    }

    @Test
    void deleteFaculty_shouldThrowExceptionWhenFacultyNotFound() {
        // given
        when(facultyRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.empty());
        when(programService.deleteAllProgramsByFacultyIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Flux.empty());

        // when
        Mono<Boolean> result = facultyService.deleteFaculty(ID_1, UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteAllFacultiesByUniversityId() {
        // when
        var faculty1 = createFaculty(ID_1);
        var faculty2 = createFaculty(ID_2);
        when(facultyRepository.findAllByUniversityId(UNIVERSITY_ID)).thenReturn(Flux.just(ID_1, ID_2));
        when(facultyRepository.findByIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Mono.just(faculty1));
        when(facultyRepository.findByIdAndUniversityId(ID_2, UNIVERSITY_ID)).thenReturn(Mono.just(faculty2));
        when(programService.deleteAllProgramsByFacultyIdAndUniversityId(ID_1, UNIVERSITY_ID)).thenReturn(Flux.just());
        when(programService.deleteAllProgramsByFacultyIdAndUniversityId(ID_2, UNIVERSITY_ID)).thenReturn(Flux.just(true, true));
        when(facultyRepository.deleteById(anyInt())).thenReturn(Mono.empty());

        // given
        Flux<Boolean> result = facultyService.deleteAllFacultiesByUniversityId(UNIVERSITY_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectNextCount(2)
                    .verifyComplete();
    }

    private FacultyEntity createFaculty(int id) {
        return FacultyEntity.builder()
                            .id(id)
                            .name("name")
                            .email("example@gmail.com")
                            .universityId(UNIVERSITY_ID)
                            .build();
    }

    private FacultyResponseDto createFacultyResponseDto(int id) {
        return FacultyResponseDto.builder()
                                 .id(id)
                                 .name("name")
                                 .email("example@gmail.com")
                                 .universityId(UNIVERSITY_ID)
                                 .build();
    }
}