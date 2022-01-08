package com.erapulus.server.student.service;

import com.erapulus.server.applicationuser.database.ApplicationUserEntity;
import com.erapulus.server.common.database.UserType;
import com.erapulus.server.common.service.AzureStorageService;
import com.erapulus.server.security.JwtAuthenticatedUser;
import com.erapulus.server.student.database.StudentEntity;
import com.erapulus.server.student.database.StudentRepository;
import com.erapulus.server.student.dto.StudentListDto;
import com.erapulus.server.student.dto.StudentRequestDto;
import com.erapulus.server.student.dto.StudentResponseDto;
import com.erapulus.server.student.dto.StudentUniversityUpdateDto;
import com.erapulus.server.student.mapper.StudentEntityToResponseDtoMapper;
import com.erapulus.server.student.mapper.StudentRequestDtoToEntityMapper;
import com.erapulus.server.university.database.UniversityEntity;
import com.erapulus.server.university.database.UniversityRepository;
import com.erapulus.server.university.dto.UniversityResponseDto;
import com.erapulus.server.university.mapper.UniversityEntityToResponseDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    private final static int ID_1 = 1;
    private final static int ID_2 = 2;
    private static final int UNIVERSITY_ID = 3;
    private static final int UNIVERSITY_ID_2 = 4;
    private static final String EMAIL = "example@gmail.com";
    private static final String PICTURE_URL = "https://example.com";
    @Mock
    StudentRepository studentRepository;

    @Mock
    UniversityRepository universityRepository;

    @Mock
    AzureStorageService azureStorageService;

    StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(studentRepository,
                new StudentRequestDtoToEntityMapper(),
                new StudentEntityToResponseDtoMapper(),
                universityRepository,
                azureStorageService,
                new UniversityEntityToResponseDtoMapper());
    }

    @Test
    void listStudents_shouldReturnStudentList() {
        // given
        var student1 = createStudent(ID_1);
        var student2 = createStudent(ID_2);
        String name = "John";
        when(studentRepository.findAllByNameAndUniversityId(name, UNIVERSITY_ID)).thenReturn(Flux.just(student1, student2));
        try (MockedStatic<ReactiveSecurityContextHolder> utilities = mockStatic(ReactiveSecurityContextHolder.class)) {
            utilities.when(ReactiveSecurityContextHolder::getContext).thenReturn(createSecurityContext());

            // when
            Mono<List<StudentListDto>> result = studentService.listStudents(name);

            // then
            StepVerifier.create(result)
                        .expectSubscription()
                        .assertNext(students -> assertEquals(2, students.size()))
                        .verifyComplete();
        }
    }

    @Test
    void listStudents_shouldReturnEmptyListWhenNoName() {
        // given
        String name = "";

        // when
        Mono<List<StudentListDto>> result = studentService.listStudents(name);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(students -> assertEquals(0, students.size()))
                    .verifyComplete();
    }

    @Test
    void getStudentById_shouldReturnStudentWhenFound() {
        // given
        var student = createStudent(ID_1);
        when(studentRepository.findByIdAndType(ID_1)).thenReturn(Mono.just(student));

        // when
        Mono<StudentResponseDto> result = studentService.getStudentById(ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(studentResponseDto -> {
                        assertEquals(ID_1, studentResponseDto.id());
                        assertEquals(UNIVERSITY_ID, studentResponseDto.universityId());
                    })
                    .verifyComplete();
    }

    @Test
    void getStudentById_shouldThrowExceptionWhenStudentNotFound() {
        // given
        when(studentRepository.findByIdAndType(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<StudentResponseDto> result = studentService.getStudentById(ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void updateStudent_shouldUpdateStudentWhenFound() {
        // given
        var student = createStudent(ID_1);
        var studentRequestDto = new StudentRequestDto();
        when(studentRepository.findByIdAndType(ID_1)).thenReturn(Mono.just(student));
        when(studentRepository.save(any(StudentEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, StudentEntity.class).id(ID_1)));

        // when
        Mono<StudentResponseDto> result = studentService.updateStudent(studentRequestDto, ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(studentResponseDto -> {
                        assertEquals(ID_1, studentResponseDto.id());
                        assertEquals(PICTURE_URL, studentResponseDto.pictureUrl());
                        assertEquals(UNIVERSITY_ID, studentResponseDto.universityId());
                    })
                    .verifyComplete();
    }

    @Test
    void updateStudent_shouldThrowExceptionWhenStudentNotFound() {
        // given
        var studentRequestDto = new StudentRequestDto();
        when(studentRepository.findByIdAndType(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<StudentResponseDto> result = studentService.updateStudent(studentRequestDto, ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void updateStudentUniversity_shouldUpdateStudentUniversity() {
        // given
        var student = createStudent(ID_1);
        var university = createUniversity();
        var studentUniversityUpdateDto = new StudentUniversityUpdateDto(UNIVERSITY_ID_2);
        when(studentRepository.findByIdAndType(ID_1)).thenReturn(Mono.just(student));
        when(universityRepository.findById(UNIVERSITY_ID_2)).thenReturn(Mono.just(university));
        when(studentRepository.save(any())).thenReturn(Mono.just(student));

        // when
        Mono<UniversityResponseDto> result = studentService.updateStudentUniversity(studentUniversityUpdateDto, ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(universityResponseDto -> assertEquals(UNIVERSITY_ID_2, universityResponseDto.id()))
                    .verifyComplete();
    }

    @Test
    void updateStudentUniversity_shouldThrowExceptionWhenStudentNotExists() {
        // given
        var studentUniversityUpdateDto = new StudentUniversityUpdateDto(UNIVERSITY_ID_2);
        when(studentRepository.findByIdAndType(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<UniversityResponseDto> result = studentService.updateStudentUniversity(studentUniversityUpdateDto, ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void updateStudentUniversity_shouldThrowExceptionWhenUniversityNotExists() {
        // given
        var student = createStudent(ID_1);
        var studentUniversityUpdateDto = new StudentUniversityUpdateDto(UNIVERSITY_ID_2);
        when(studentRepository.findByIdAndType(ID_1)).thenReturn(Mono.just(student));
        when(universityRepository.findById(UNIVERSITY_ID_2)).thenReturn(Mono.empty());

        // when
        Mono<UniversityResponseDto> result = studentService.updateStudentUniversity(studentUniversityUpdateDto, ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void updateStudentPhoto_shouldUpdateStudentPhoto() {
        // given
        var student = createStudent(ID_1);
        var filePart = mock(FilePart.class);
        var path = "user/1/photo/example.png";
        var fullPath = "https://azure.com/user/1/photo/example.png";
        when(filePart.filename()).thenReturn("example.png");
        when(studentRepository.findByIdAndType(ID_1)).thenReturn(Mono.just(student));
        when(azureStorageService.uploadFile(filePart, path)).thenReturn(Mono.just(fullPath));
        when(studentRepository.save(student.pictureUrl(path)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, StudentEntity.class)));

        // when
        Mono<StudentResponseDto> result = studentService.updateStudentPhoto(ID_1, filePart);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(studentResponseDto -> {
                        assertEquals(ID_1, studentResponseDto.id());
                        assertEquals(fullPath, studentResponseDto.pictureUrl());
                        assertEquals(UNIVERSITY_ID, studentResponseDto.universityId());
                    })
                    .verifyComplete();
    }

    @Test
    void updateStudentPhoto_shouldThrowExceptionWhenUserNotFound() {
        // given
        var filePart = mock(FilePart.class);
        when(filePart.filename()).thenReturn("example.png");
        when(studentRepository.findByIdAndType(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<StudentResponseDto> result = studentService.updateStudentPhoto(ID_1, filePart);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void updateStudentPhoto_shouldThrowExceptionWhenUploadFail() {
        // given
        var student = createStudent(ID_1);
        var filePart = mock(FilePart.class);
        var path = "user/1/photo/example.png";
        when(filePart.filename()).thenReturn("example.png");
        when(studentRepository.findByIdAndType(ID_1)).thenReturn(Mono.just(student));
        when(azureStorageService.uploadFile(filePart, path)).thenReturn(Mono.error(new RuntimeException()));

        // when
        Mono<StudentResponseDto> result = studentService.updateStudentPhoto(ID_1, filePart);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(RuntimeException.class)
                    .verify();
    }

    private StudentEntity createStudent(int id) {
        return StudentEntity.builder()
                            .id(id)
                            .firstName("John")
                            .lastName("John")
                            .email(EMAIL)
                            .universityId(UNIVERSITY_ID)
                            .pictureUrl(PICTURE_URL)
                            .build();
    }

    private Mono<SecurityContext> createSecurityContext() {
        return Mono.just(new SecurityContextImpl(
                new JwtAuthenticatedUser(
                        ApplicationUserEntity.builder()
                                             .id(ID_1)
                                             .type(UserType.STUDENT)
                                             .firstName("firstName")
                                             .lastName("lastName")
                                             .universityId(UNIVERSITY_ID)
                                             .build(),
                        Collections.emptyList())));
    }

    private UniversityEntity createUniversity() {
        return UniversityEntity.builder()
                               .id(UNIVERSITY_ID_2)
                               .name("name")
                               .build();
    }
}