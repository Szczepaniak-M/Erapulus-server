package com.erapulus.server.faculty.database;

import com.erapulus.server.university.database.UniversityEntity;
import com.erapulus.server.university.database.UniversityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FacultyRepositoryTest {

    private static final String FACULTY_1 = "faculty1";
    private static final String FACULTY_2 = "faculty2";
    private static final String FACULTY_21 = "faculty21";
    private static final String FACULTY_3 = "faculty3";
    private static final String UNIVERSITY_1 = "university1";
    private static final String UNIVERSITY_2 = "university2";

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @AfterEach
    void clean() {
        facultyRepository.deleteAll().block();
        universityRepository.deleteAll().block();
    }

    @Test
    void findByUniversityIdAndName_shouldReturnFacultiesForGivenUniversity() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var faculty1 = createFaculty(FACULTY_1, university1);
        var faculty2 = createFaculty(FACULTY_2, university1);
        var faculty3 = createFaculty(FACULTY_1, university2);
        var faculty4 = createFaculty(FACULTY_2, university2);
        var pageRequest = PageRequest.of(0, 4);

        // when
        var result = facultyRepository.findByUniversityIdAndName(university1.id(), null, pageRequest.getOffset(), pageRequest.getPageSize());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(faculties -> faculties.stream().map(FacultyEntity::id).toList().size() == 2)
                    .expectRecordedMatches(faculties -> faculties.stream().map(FacultyEntity::id).toList().containsAll(List.of(faculty1.id(), faculty2.id())))
                    .verifyComplete();
    }

    @Test
    void findByUniversityIdAndName_shouldReturnFacultiesForGivenUniversityAndName() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var faculty1 = createFaculty(FACULTY_1, university1);
        var faculty2 = createFaculty(FACULTY_21, university1);
        var faculty3 = createFaculty(FACULTY_1, university2);
        var faculty4 = createFaculty(FACULTY_2, university2);
        var pageRequest = PageRequest.of(0, 4);

        // when
        var result = facultyRepository.findByUniversityIdAndName(university1.id(), FACULTY_2, pageRequest.getOffset(), pageRequest.getPageSize());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(faculties -> faculties.stream().map(FacultyEntity::id).toList().size() == 1)
                    .expectRecordedMatches(faculties -> faculties.stream().map(FacultyEntity::id).toList().contains(faculty2.id()))
                    .verifyComplete();
    }

    @Test
    void findByUniversityIdAndName_shouldReturnFacultiesWhenSecondPageRequested() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty1 = createFaculty(FACULTY_1, university);
        var faculty2 = createFaculty(FACULTY_2, university);
        var faculty3 = createFaculty(FACULTY_3, university);
        var pageRequest = PageRequest.of(1, 2);

        // when
        var result = facultyRepository.findByUniversityIdAndName(university.id(), null, pageRequest.getOffset(), pageRequest.getPageSize());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(faculties -> faculties.stream().map(FacultyEntity::id).toList().size() == 1)
                    .expectRecordedMatches(faculties -> faculties.stream().map(FacultyEntity::id).toList().contains(faculty3.id()))
                    .verifyComplete();
    }

    @Test
    void countByUniversityIdAndName_shouldReturnFacultiesNumberForGivenUniversity() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty1 = createFaculty(FACULTY_1, university);
        var faculty2 = createFaculty(FACULTY_2, university);
        int expectedResult = 2;

        // when
        var result = facultyRepository.countByUniversityIdAndName(university.id(), null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(facultyCount -> assertEquals(expectedResult, facultyCount))
                    .verifyComplete();
    }

    @Test
    void countByUniversityIdAndName_shouldReturnFacultiesNumberForGivenUniversityAndName() {
        // given
        var university = createUniversity(UNIVERSITY_1);
        var faculty1 = createFaculty(FACULTY_1, university);
        var faculty2 = createFaculty(FACULTY_21, university);
        int expectedResult = 1;

        // when
        var result = facultyRepository.countByUniversityIdAndName(university.id(), FACULTY_2);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(facultyCount -> assertEquals(expectedResult, facultyCount))
                    .verifyComplete();
    }

    @Test
    void findByIdAndUniversityId_shouldReturnFacultyWhenUniversityAndIdExists() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var faculty1 = createFaculty(FACULTY_1, university1);
        var faculty2 = createFaculty(FACULTY_2, university1);
        var faculty3 = createFaculty(FACULTY_1, university2);
        var faculty4 = createFaculty(FACULTY_2, university2);

        // when
        Mono<FacultyEntity> result = facultyRepository.findByIdAndUniversityId(faculty1.id(), university1.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(facultyFromDatabase -> assertEquals(faculty1.id(), facultyFromDatabase.id()))
                    .verifyComplete();
    }

    @Test
    void findByIdAndUniversityId_shouldReturnEmptyMonoWhenWrongUniversity() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var faculty1 = createFaculty(FACULTY_1, university1);
        var faculty2 = createFaculty(FACULTY_1, university2);

        // when
        Mono<FacultyEntity> result = facultyRepository.findByIdAndUniversityId(faculty1.id(), university2.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    @Test
    void findAllByUniversityId_shouldReturnFacultyWithGivenUniversity() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var faculty1 = createFaculty(FACULTY_1, university1);
        var faculty2 = createFaculty(FACULTY_2, university1);
        var faculty3 = createFaculty(FACULTY_1, university2);
        var faculty4 = createFaculty(FACULTY_2, university2);

        // when
        Flux<Integer> result = facultyRepository.findAllByUniversityId(university1.id());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(faculties -> faculties.size() == 2)
                    .expectRecordedMatches(faculties -> faculties.containsAll(List.of(faculty1.id(), faculty2.id())))
                    .verifyComplete();
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
                                                   .email("example@gmail.com")
                                                   .build();
        return facultyRepository.save(facultyEntity).block();
    }
}
