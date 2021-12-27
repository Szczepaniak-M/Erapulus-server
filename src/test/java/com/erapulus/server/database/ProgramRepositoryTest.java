package com.erapulus.server.database;

import com.erapulus.server.database.model.FacultyEntity;
import com.erapulus.server.database.model.ProgramEntity;
import com.erapulus.server.database.model.UniversityEntity;
import com.erapulus.server.database.repository.FacultyRepository;
import com.erapulus.server.database.repository.ProgramRepository;
import com.erapulus.server.database.repository.UniversityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ProgramRepositoryTest {

    private static final String UNIVERSITY_1 = "university1";
    private static final String UNIVERSITY_2 = "university2";
    private static final String FACULTY_1 = "faculty1";
    private static final String FACULTY_2 = "faculty2";
    private static final String PROGRAM_1 = "program1";
    private static final String PROGRAM_2 = "program2";
    private static final String PROGRAM_21 = "program21";
    private static final String PROGRAM_3 = "program3";

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @AfterEach
    void clean() {
        programRepository.deleteAll().block();
        facultyRepository.deleteAll().block();
        universityRepository.deleteAll().block();
    }

    @Test
    void findByFacultyIdAndName_shouldReturnProgramForGivenFaculty() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty1 = createFaculty(FACULTY_1, university);
        var faculty2 = createFaculty(FACULTY_2, university);
        var program1 = createProgram(PROGRAM_1, faculty1);
        var program2 = createProgram(PROGRAM_2, faculty1);
        var program3 = createProgram(PROGRAM_1, faculty2);
        var program4 = createProgram(PROGRAM_2, faculty2);
        var pageRequest = PageRequest.of(0, 4);

        // when
        var result = programRepository.findByFacultyIdAndName(faculty1.id(), null, pageRequest.getOffset(), pageRequest.getPageSize());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(programs -> programs.stream().map(ProgramEntity::id).toList().size() == 2)
                    .expectRecordedMatches(programs -> programs.stream().map(ProgramEntity::id).toList().containsAll(List.of(program1.id(), program2.id())))
                    .verifyComplete();
    }

    @Test
    void findByFacultyIdAndName_shouldReturnProgramForGivenFacultyAndName() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty1 = createFaculty(FACULTY_1, university);
        var faculty2 = createFaculty(FACULTY_2, university);
        var program1 = createProgram(PROGRAM_1, faculty1);
        var program2 = createProgram(PROGRAM_21, faculty1);
        var program3 = createProgram(PROGRAM_1, faculty2);
        var program4 = createProgram(PROGRAM_2, faculty2);
        var pageRequest = PageRequest.of(0, 4);

        // when
        var result = programRepository.findByFacultyIdAndName(faculty1.id(), PROGRAM_2, pageRequest.getOffset(), pageRequest.getPageSize());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(programs -> programs.stream().map(ProgramEntity::id).toList().size() == 1)
                    .expectRecordedMatches(programs -> programs.stream().map(ProgramEntity::id).toList().contains(program2.id()))
                    .verifyComplete();
    }

    @Test
    void findByFacultyIdAndName_shouldReturnProgramsWhenSecondPageRequested() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty = createFaculty(FACULTY_1, university);
        var program1 = createProgram(PROGRAM_1, faculty);
        var program2 = createProgram(PROGRAM_2, faculty);
        var program3 = createProgram(PROGRAM_3, faculty);
        var pageRequest = PageRequest.of(1, 2);

        // when
        var result = programRepository.findByFacultyIdAndName(faculty.id(), null, pageRequest.getOffset(), pageRequest.getPageSize());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(programs -> programs.stream().map(ProgramEntity::id).toList().size() == 1)
                    .expectRecordedMatches(programs -> programs.stream().map(ProgramEntity::id).toList().contains(program3.id()))
                    .verifyComplete();
    }

    @Test
    void countByFacultyIdAndName_shouldReturnProgramNumberForGivenFaculty() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty = createFaculty(FACULTY_1, university);
        var program1 = createProgram(PROGRAM_1, faculty);
        var program2 = createProgram(PROGRAM_2, faculty);
        int expectedResult = 2;

        // when
        var result = programRepository.countByFacultyIdAndName(faculty.id(), null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(programCount -> assertEquals(expectedResult, programCount))
                    .verifyComplete();
    }

    @Test
    void countByFacultyIdAndName_shouldReturnProgramNumberForGivenFacultyAndName() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty = createFaculty(FACULTY_1, university);
        var program1 = createProgram(PROGRAM_1, faculty);
        var program2 = createProgram(PROGRAM_21, faculty);
        int expectedResult = 1;

        // when
        var result = programRepository.countByFacultyIdAndName(faculty.id(), PROGRAM_2);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(programCount -> assertEquals(expectedResult, programCount))
                    .verifyComplete();
    }

    @Test
    void findByIdAndUniversityIdAndFacultyId_shouldReturnProgramWhenUniversityAndFacultyAndIdExist() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty1 = createFaculty(FACULTY_1, university);
        var faculty2 = createFaculty(FACULTY_2, university);
        var program1 = createProgram(PROGRAM_1, faculty1);
        var program2 = createProgram(PROGRAM_2, faculty1);
        var program3 = createProgram(PROGRAM_1, faculty2);
        var program4 = createProgram(PROGRAM_2, faculty2);

        // when
        Mono<ProgramEntity> result = programRepository.findByIdAndUniversityIdAndFacultyId(program1.id(), university.id(), faculty1.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(programFromDatabase -> assertEquals(program1.id(), programFromDatabase.id()))
                    .verifyComplete();
    }

    @Test
    void findByIdAndUniversityIdAndFacultyId_shouldReturnEmptyMonoWhenWrongFaculty() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty1 = createFaculty(FACULTY_1, university);
        var faculty2 = createFaculty(FACULTY_2, university);
        var program1 = createProgram(PROGRAM_1, faculty1);
        var program2 = createProgram(PROGRAM_2, faculty1);
        var program3 = createProgram(PROGRAM_1, faculty2);
        var program4 = createProgram(PROGRAM_2, faculty2);

        // when
        Mono<ProgramEntity> result = programRepository.findByIdAndUniversityIdAndFacultyId(program1.id(), university.id(), faculty2.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    @Test
    void findByIdAndUniversityIdAndFacultyId_shouldReturnEmptyMonoWhenWrongUniversity() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var faculty = createFaculty(FACULTY_1, university1);
        var program1 = createProgram(PROGRAM_1, faculty);
        var program2 = createProgram(PROGRAM_2, faculty);

        // when
        Mono<ProgramEntity> result = programRepository.findByIdAndUniversityIdAndFacultyId(program1.id(), university2.id(), faculty.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    @Test
    void existsByIdAndUniversityIdAndFacultyIdAndProgramId_shouldReturnTrueWhenModuleExists() {
        var university = createUniversity(UNIVERSITY_1);
        var faculty = createFaculty(FACULTY_1, university);
        var program = createProgram(PROGRAM_1, faculty);

        // when
        Mono<Boolean> result = programRepository.existsByIdAndUniversityIdAndFacultyId(program.id(), university.id(), faculty.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertTrue)
                    .verifyComplete();
    }

    @Test
    void existsByIdAndUniversityIdAndFacultyIdAndProgramId_shouldReturnEmptyWhenWrongFaculty() {
        var university = createUniversity(UNIVERSITY_1);
        var faculty1 = createFaculty(FACULTY_1, university);
        var faculty2 = createFaculty(FACULTY_2, university);
        var program = createProgram(PROGRAM_1, faculty1);

        // when
        Mono<Boolean> result = programRepository.existsByIdAndUniversityIdAndFacultyId(program.id(), university.id(), faculty2.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertFalse)
                    .verifyComplete();
    }

    @Test
    void existsByIdAndUniversityIdAndFacultyIdAndProgramId_shouldReturnEmptyWhenWrongUniversity() {
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var faculty = createFaculty(FACULTY_1, university1);
        var program = createProgram(PROGRAM_1, faculty);

        // when
        Mono<Boolean> result = programRepository.existsByIdAndUniversityIdAndFacultyId(program.id(), university2.id(), faculty.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertFalse)
                    .verifyComplete();
    }

    private ProgramEntity createProgram(String name, FacultyEntity facultyEntity) {
        ProgramEntity programEntity = ProgramEntity.builder()
                                                   .name(name)
                                                   .abbrev("abbrev")
                                                   .facultyId(facultyEntity.id())
                                                   .build();
        return programRepository.save(programEntity).block();
    }

    private FacultyEntity createFaculty(String name, UniversityEntity universityEntity) {
        FacultyEntity facultyEntity = FacultyEntity.builder()
                                                   .universityId(universityEntity.id())
                                                   .name(name)
                                                   .address("address")
                                                   .build();
        return facultyRepository.save(facultyEntity).block();
    }

    private UniversityEntity createUniversity(String name) {
        UniversityEntity universityEntity = UniversityEntity.builder()
                                                            .name(name)
                                                            .address("Some address")
                                                            .zipcode("00000")
                                                            .city("city")
                                                            .country("country")
                                                            .websiteUrl("url")
                                                            .build();
        return universityRepository.save(universityEntity).block();
    }
}
