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
class ModuleRepositoryTest {

    private static final String MODULE_1 = "module1";
    private static final String MODULE_2 = "module2";
    private static final String MODULE_3 = "module3";
    private static final String PROGRAM_1 = "program1";
    private static final String PROGRAM_2 = "program2";

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
    void findByProgramId_shouldReturnModulesForGivenProgram() {
        // given
        var faculty = createFaculty();
        var program1 = createProgram(PROGRAM_1, faculty);
        var program2 = createProgram(PROGRAM_2, faculty);
        var module1 = createModule(MODULE_1, program1);
        var module2 = createModule(MODULE_2, program1);
        var module3 = createModule(MODULE_1, program2);
        var module4 = createModule(MODULE_2, program2);
        var pageRequest = PageRequest.of(0, 4);

        // when
        var result = moduleRepository.findByProgramId(program1.id(), pageRequest.getOffset(), pageRequest.getPageSize());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(modules -> modules.stream().map(ModuleEntity::id).toList().size() == 2)
                    .expectRecordedMatches(modules -> modules.stream().map(ModuleEntity::id).toList().containsAll(List.of(module1.id(), module2.id())))
                    .verifyComplete();
    }

    @Test
    void findByProgramId_shouldReturnModulesWhenSecondPageRequested() {
        // given
        var faculty = createFaculty();
        var program = createProgram(PROGRAM_1, faculty);
        var module1 = createModule(MODULE_1, program);
        var module2 = createModule(MODULE_2, program);
        var module3 = createModule(MODULE_3, program);
        var pageRequest = PageRequest.of(1, 2);

        // when
        var result = moduleRepository.findByProgramId(program.id(), pageRequest.getOffset(), pageRequest.getPageSize());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(modules -> modules.stream().map(ModuleEntity::id).toList().size() == 1)
                    .expectRecordedMatches(modules -> modules.stream().map(ModuleEntity::id).toList().contains(module3.id()))
                    .verifyComplete();
    }

    @Test
    void countByProgramId_shouldReturnModuleNumberForGivenProgram() {
        // given
        var faculty = createFaculty();
        var program = createProgram(PROGRAM_1, faculty);
        var module1 = createModule(MODULE_1, program);
        var module2 = createModule(MODULE_2, program);
        int expectedResult = 2;

        // when
        var result = moduleRepository.countByProgramId(program.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(moduleCount -> assertEquals(expectedResult, moduleCount))
                    .verifyComplete();
    }

    @Test
    void findByIdAndProgramId_shouldReturnModuleWhenProgramAndIdExists() {
        // given
        var faculty = createFaculty();
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
        var faculty = createFaculty();
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
    void findByIdAndProgramId_shouldReturnEmptyMonoWhenWrongId() {
        // given
        var faculty = createFaculty();
        var program = createProgram(PROGRAM_1, faculty);
        var module = createModule(MODULE_1, program);

        // when
        Mono<ModuleEntity> result = moduleRepository.findByIdAndProgramId(module.id() + 1, program.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
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

    private FacultyEntity createFaculty() {
        UniversityEntity universityEntity = UniversityEntity.builder()
                                                            .name("name")
                                                            .address("Some address")
                                                            .zipcode("00000")
                                                            .city("city")
                                                            .country("country")
                                                            .websiteUrl("url")
                                                            .build();
        var savedUniversity = universityRepository.save(universityEntity).block();
        FacultyEntity facultyEntity = FacultyEntity.builder()
                                                   .universityId(savedUniversity.id())
                                                   .name("name")
                                                   .address("address")
                                                   .build();
        return facultyRepository.save(facultyEntity).block();
    }
}
