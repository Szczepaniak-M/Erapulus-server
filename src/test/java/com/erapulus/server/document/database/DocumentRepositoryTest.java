package com.erapulus.server.document.database;

import com.erapulus.server.faculty.database.FacultyEntity;
import com.erapulus.server.faculty.database.FacultyRepository;
import com.erapulus.server.module.database.ModuleEntity;
import com.erapulus.server.module.database.ModuleRepository;
import com.erapulus.server.program.database.ProgramEntity;
import com.erapulus.server.program.database.ProgramRepository;
import com.erapulus.server.university.database.UniversityEntity;
import com.erapulus.server.university.database.UniversityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class DocumentRepositoryTest {

    private final static String UNIVERSITY_1 = "university1";
    private final static String UNIVERSITY_2 = "university2";
    private final static String PROGRAM_1 = "program1";
    private final static String PROGRAM_2 = "program2";
    private final static String MODULE_1 = "module1";
    private final static String MODULE_2 = "module2";

    @Autowired
    private DocumentRepository documentRepository;

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
        documentRepository.deleteAll().block();
        moduleRepository.deleteAll().block();
        programRepository.deleteAll().block();
        facultyRepository.deleteAll().block();
        universityRepository.deleteAll().block();
    }

    @Test
    void findAllByFilters_shouldReturnDocumentForUniversity() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var document1 = createDocument(university1.id(), null, null);
        var document2 = createDocument(university1.id(), null, null);
        var document3 = createDocument(university2.id(), null, null);
        var document4 = createDocument(university2.id(), null, null);

        // when
        Flux<DocumentEntity> result = documentRepository.findAllByFilters(university1.id(), null, null);

        //then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(documents -> documents.stream().map(DocumentEntity::id).toList().size() == 2)
                    .expectRecordedMatches(documents -> documents.stream().map(DocumentEntity::id).toList().containsAll(List.of(document1.id(), document2.id())))
                    .verifyComplete();
    }

    @Test
    void findAllByFilters_shouldReturnDocumentForProgram() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty = createFaculty(university);
        var program1 = createProgram(PROGRAM_1, faculty);
        var program2 = createProgram(PROGRAM_2, faculty);
        var document1 = createDocument(null, program1.id(), null);
        var document2 = createDocument(null, program1.id(), null);
        var document3 = createDocument(null, program2.id(), null);
        var document4 = createDocument(null, program2.id(), null);

        // when
        Flux<DocumentEntity> result = documentRepository.findAllByFilters(null, program1.id(), null);

        //then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(documents -> documents.stream().map(DocumentEntity::id).toList().size() == 2)
                    .expectRecordedMatches(documents -> documents.stream().map(DocumentEntity::id).toList().containsAll(List.of(document1.id(), document2.id())))
                    .verifyComplete();
    }

    @Test
    void findAllByFilters_shouldReturnDocumentForModule() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty = createFaculty(university);
        var program = createProgram(PROGRAM_1, faculty);
        var module1 = createModule(MODULE_1, program);
        var module2 = createModule(MODULE_2, program);
        var document1 = createDocument(null, null, module1.id());
        var document2 = createDocument(null, null, module1.id());
        var document3 = createDocument(null, null, module2.id());
        var document4 = createDocument(null, null, module2.id());

        // when
        Flux<DocumentEntity> result = documentRepository.findAllByFilters(null, null, module1.id());

        //then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(documents -> documents.stream().map(DocumentEntity::id).toList().size() == 2)
                    .expectRecordedMatches(documents -> documents.stream().map(DocumentEntity::id).toList().containsAll(List.of(document1.id(), document2.id())))
                    .verifyComplete();
    }

    private DocumentEntity createDocument(Integer universityId, Integer programId, Integer moduleId) {
        DocumentEntity documentEntity = DocumentEntity.builder()
                                                      .name("file")
                                                      .path("path")
                                                      .universityId(universityId)
                                                      .programId(programId)
                                                      .moduleId(moduleId)
                                                      .build();
        return documentRepository.save(documentEntity).block();
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

    private FacultyEntity createFaculty(UniversityEntity universityEntity) {
        FacultyEntity facultyEntity = FacultyEntity.builder()
                                                   .universityId(universityEntity.id())
                                                   .name("faculty")
                                                   .address("address")
                                                   .email("example@gmail.com")
                                                   .build();
        return facultyRepository.save(facultyEntity).block();
    }

    private ProgramEntity createProgram(String name, FacultyEntity facultyEntity) {
        ProgramEntity programEntity = ProgramEntity.builder()
                                                   .name(name)
                                                   .abbrev("abbrev")
                                                   .facultyId(facultyEntity.id())
                                                   .build();
        return programRepository.save(programEntity).block();
    }

    private ModuleEntity createModule(String name, ProgramEntity programEntity) {
        ModuleEntity moduleEntity = ModuleEntity.builder()
                                                .name(name)
                                                .abbrev("abbrev")
                                                .programId(programEntity.id())
                                                .build();
        return moduleRepository.save(moduleEntity).block();
    }
}
