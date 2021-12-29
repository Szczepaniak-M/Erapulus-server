package com.erapulus.server.database;

import com.erapulus.server.database.model.FacultyEntity;
import com.erapulus.server.database.model.ModuleEntity;
import com.erapulus.server.database.model.ProgramEntity;
import com.erapulus.server.database.model.UniversityEntity;
import com.erapulus.server.database.repository.FacultyRepository;
import com.erapulus.server.database.repository.ModuleRepository;
import com.erapulus.server.database.repository.ProgramRepository;
import com.erapulus.server.database.repository.UniversityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ModuleRepositoryTest {

    private static final String MODULE_1 = "module1";
    private static final String MODULE_2 = "module2";
    private static final String MODULE_22 = "module22";
    private static final String MODULE_3 = "module3";
    private static final String PROGRAM_1 = "program1";
    private static final String PROGRAM_2 = "program2";
    private static final String FACULTY_1 = "faculty1";
    private static final String FACULTY_2 = "faculty2";
    private static final String UNIVERSITY_1 = "university1";
    private static final String UNIVERSITY_2 = "university2";

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @AfterEach
    void clean() {
        moduleRepository.deleteAll().block();
        programRepository.deleteAll().block();
        facultyRepository.deleteAll().block();
        universityRepository.deleteAll().block();
    }

    @Test
    void findByProgramIdAndName_shouldReturnModulesForGivenProgram() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty = createFaculty(FACULTY_1, university);
        var program1 = createProgram(PROGRAM_1, faculty);
        var program2 = createProgram(PROGRAM_2, faculty);
        var module1 = createModule(MODULE_1, program1);
        var module2 = createModule(MODULE_2, program1);
        var module3 = createModule(MODULE_1, program2);
        var module4 = createModule(MODULE_2, program2);
        var pageRequest = PageRequest.of(0, 4);

        // when
        var result = moduleRepository.findByProgramIdAndName(program1.id(), null, pageRequest.getOffset(), pageRequest.getPageSize());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(modules -> modules.stream().map(ModuleEntity::id).toList().size() == 2)
                    .expectRecordedMatches(modules -> modules.stream().map(ModuleEntity::id).toList().containsAll(List.of(module1.id(), module2.id())))
                    .verifyComplete();
    }

    @Test
    void findByProgramIdAndName_shouldReturnModulesForGivenProgramAndName() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty = createFaculty(FACULTY_1, university);
        var program1 = createProgram(PROGRAM_1, faculty);
        var program2 = createProgram(PROGRAM_2, faculty);
        var module1 = createModule(MODULE_1, program1);
        var module2 = createModule(MODULE_22, program1);
        var module3 = createModule(MODULE_1, program2);
        var module4 = createModule(MODULE_2, program2);
        var pageRequest = PageRequest.of(0, 4);

        // when
        var result = moduleRepository.findByProgramIdAndName(program1.id(), MODULE_2, pageRequest.getOffset(), pageRequest.getPageSize());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(modules -> modules.stream().map(ModuleEntity::id).toList().size() == 1)
                    .expectRecordedMatches(modules -> modules.stream().map(ModuleEntity::id).toList().contains(module2.id()))
                    .verifyComplete();
    }

    @Test
    void findByProgramIdAndName_shouldReturnModulesWhenSecondPageRequested() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty = createFaculty(FACULTY_1, university);
        var program = createProgram(PROGRAM_1, faculty);
        var module1 = createModule(MODULE_1, program);
        var module2 = createModule(MODULE_2, program);
        var module3 = createModule(MODULE_3, program);
        var pageRequest = PageRequest.of(1, 2);

        // when
        var result = moduleRepository.findByProgramIdAndName(program.id(), null, pageRequest.getOffset(), pageRequest.getPageSize());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(modules -> modules.stream().map(ModuleEntity::id).toList().size() == 1)
                    .expectRecordedMatches(modules -> modules.stream().map(ModuleEntity::id).toList().contains(module3.id()))
                    .verifyComplete();
    }

    @Test
    void countByProgramIdAndName_shouldReturnModuleNumberForGivenProgram() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty = createFaculty(FACULTY_1, university);
        var program = createProgram(PROGRAM_1, faculty);
        var module1 = createModule(MODULE_1, program);
        var module2 = createModule(MODULE_2, program);
        int expectedResult = 2;

        // when
        var result = moduleRepository.countByProgramIdAndName(program.id(), null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(moduleCount -> assertEquals(expectedResult, moduleCount))
                    .verifyComplete();
    }

    @Test
    void countByProgramIdAndName_shouldReturnModuleNumberForGivenProgramAndName() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty = createFaculty(FACULTY_1, university);
        var program = createProgram(PROGRAM_1, faculty);
        var module1 = createModule(MODULE_1, program);
        var module2 = createModule(MODULE_22, program);
        int expectedResult = 1;

        // when
        var result = moduleRepository.countByProgramIdAndName(program.id(), MODULE_2);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(moduleCount -> assertEquals(expectedResult, moduleCount))
                    .verifyComplete();
    }

    @Test
    void findByIdAndProgramId_shouldReturnModuleWhenProgramAndIdExists() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty = createFaculty(FACULTY_1, university);
        var program1 = createProgram(PROGRAM_1, faculty);
        var program2 = createProgram(PROGRAM_2, faculty);
        var module1 = createModule(MODULE_1, program1);
        var module2 = createModule(MODULE_2, program1);
        var module3 = createModule(MODULE_1, program2);
        var module4 = createModule(MODULE_2, program2);

        // when
        Mono<ModuleEntity> result = moduleRepository.findByIdAndProgramId(module1.id(), program1.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(moduleFromDatabase -> assertEquals(module1.id(), moduleFromDatabase.id()))
                    .verifyComplete();
    }

    @Test
    void findByIdAndProgramId_shouldReturnEmptyMonoWhenWrongProgram() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty = createFaculty(FACULTY_1, university);
        var program1 = createProgram(PROGRAM_1, faculty);
        var program2 = createProgram(PROGRAM_2, faculty);
        var module1 = createModule(MODULE_1, program1);
        var module2 = createModule(MODULE_1, program2);

        // when
        Mono<ModuleEntity> result = moduleRepository.findByIdAndProgramId(module1.id(), program2.id());

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
        var module = createModule(MODULE_1, program);

        // when
        Mono<Boolean> result = moduleRepository.existsByIdAndUniversityIdAndFacultyIdAndProgramId(module.id(), university.id(), faculty.id(), program.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertTrue)
                    .verifyComplete();
    }

    @Test
    void existsByIdAndUniversityIdAndFacultyIdAndProgramId_shouldReturnFalseWhenProgramNotExists() {
        var university = createUniversity(UNIVERSITY_1);
        var faculty = createFaculty(FACULTY_1, university);
        var program1 = createProgram(PROGRAM_1, faculty);
        var program2 = createProgram(PROGRAM_2, faculty);
        var module = createModule(MODULE_1, program1);

        // when
        Mono<Boolean> result = moduleRepository.existsByIdAndUniversityIdAndFacultyIdAndProgramId(module.id(), university.id(), faculty.id(), program2.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertFalse)
                    .verifyComplete();
    }

    @Test
    void existsByIdAndUniversityIdAndFacultyIdAndProgramId_shouldReturnFalseWhenFacultyNotExists() {
        var university = createUniversity(UNIVERSITY_1);
        var faculty1 = createFaculty(FACULTY_1, university);
        var faculty2 = createFaculty(FACULTY_2, university);
        var program = createProgram(PROGRAM_1, faculty1);
        var module = createModule(MODULE_1, program);

        // when
        Mono<Boolean> result = moduleRepository.existsByIdAndUniversityIdAndFacultyIdAndProgramId(module.id(), university.id(), faculty2.id(), program.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertFalse)
                    .verifyComplete();
    }

    @Test
    void existsByIdAndUniversityIdAndFacultyIdAndProgramId_shouldReturnFalseWhenUniversityNotExists() {
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var faculty = createFaculty(FACULTY_1, university1);
        var program = createProgram(PROGRAM_1, faculty);
        var module = createModule(MODULE_1, program);

        // when
        Mono<Boolean> result = moduleRepository.existsByIdAndUniversityIdAndFacultyIdAndProgramId(module.id(), university2.id(), faculty.id(), program.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertFalse)
                    .verifyComplete();
    }

    @Test
    void findAllByProgramId_shouldReturnModulesWithGivenProgram() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty = createFaculty(FACULTY_1, university);
        var program1 = createProgram(PROGRAM_1, faculty);
        var program2 = createProgram(PROGRAM_2, faculty);
        var module1 = createModule(MODULE_1, program1);
        var module2 = createModule(MODULE_2, program1);
        var module3 = createModule(MODULE_1, program2);
        var module4 = createModule(MODULE_2, program2);

        // when
        Flux<Integer> result = moduleRepository.findAllByProgramId(program1.id());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(modules -> modules.size() == 2)
                    .expectRecordedMatches(modules -> modules.containsAll(List.of(module1.id(), module2.id())))
                    .verifyComplete();
    }

    private ModuleEntity createModule(String name, ProgramEntity programEntity) {
        ModuleEntity moduleEntity = ModuleEntity.builder()
                                                .name(name)
                                                .abbrev("abbrev")
                                                .programId(programEntity.id())
                                                .build();
        return moduleRepository.save(moduleEntity).block();
    }

    private ProgramEntity createProgram(String name, FacultyEntity facultyEntity) {
        ProgramEntity programEntity = ProgramEntity.builder()
                                                   .name(name)
                                                   .abbrev("abbrev")
                                                   .facultyId(facultyEntity.id())
                                                   .build();
        return programRepository.save(programEntity).block();
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

    private FacultyEntity createFaculty(String name, UniversityEntity universityEntity) {
        FacultyEntity facultyEntity = FacultyEntity.builder()
                                                   .universityId(universityEntity.id())
                                                   .name(name)
                                                   .address("address")
                                                   .build();
        return facultyRepository.save(facultyEntity).block();
    }
}
