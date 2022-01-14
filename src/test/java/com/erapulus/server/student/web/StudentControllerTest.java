package com.erapulus.server.student.web;

import com.erapulus.server.TestUtils;
import com.erapulus.server.student.dto.StudentListDto;
import com.erapulus.server.student.dto.StudentRequestDto;
import com.erapulus.server.student.dto.StudentResponseDto;
import com.erapulus.server.student.dto.StudentUniversityUpdateDto;
import com.erapulus.server.student.service.StudentService;
import com.erapulus.server.university.dto.UniversityResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {StudentRouter.class, StudentController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class StudentControllerTest {

    private final static int STUDENT_ID_1 = 1;
    private final static int STUDENT_ID_2 = 2;
    private final static Integer UNIVERSITY_ID = 3;

    @Autowired
    ApplicationContext applicationContext;

    @MockBean
    StudentService studentService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    void listStudents_shouldReturnStudents() {
        // given
        var studentList = List.of(createStudentListDto(STUDENT_ID_1), createStudentListDto(STUDENT_ID_2));
        String expectedPayload = """
                [
                  {
                    "id":1,
                    "firstName":"firstName",
                    "lastName":"lastName",
                    "pictureUrl":"pictureUrl"
                  },
                  {
                    "id":2,
                    "firstName":"firstName",
                    "lastName":"lastName",
                    "pictureUrl":"pictureUrl"
                  }
                ]""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(studentService.listStudents("name")).thenReturn(Mono.just(studentList));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student")
                             .queryParam("name", "name")
                             .build(UNIVERSITY_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void listStudents_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(studentService.listStudents("name")).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student")
                             .queryParam("name", "name")
                             .build(UNIVERSITY_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getStudentById_shouldReturnStudent() {
        // given
        var studentResponseDto = createStudentResponseDto();
        String expectedPayload = """
                {
                   "id":1,
                   "firstName":"firstName",
                   "lastName":"lastName",
                   "universityId":3,
                   "email":"example@gmail.com",
                   "phoneNumber":"+48 123 456 789",
                   "pictureUrl":"pictureUrl",
                   "facebookUrl":"facebookUrl",
                   "whatsUpUrl":"whatsUpUrl",
                   "instagramUsername":"instagramUserName"
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(studentService.getStudentById(STUDENT_ID_1)).thenReturn(Mono.just(studentResponseDto));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}")
                             .build(STUDENT_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getStudentById_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "student.not.found");
        when(studentService.getStudentById(STUDENT_ID_1)).thenReturn(Mono.error(new NoSuchElementException("student")));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}")
                             .build(STUDENT_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getStudentById_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(studentService.getStudentById(STUDENT_ID_1)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}")
                             .build(STUDENT_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudentWhenDataCorrect() {
        // given
        var studentRequestDto = createStudentRequestDto();
        var studentResponseDto = createStudentResponseDto();
        String expectedPayload = """
                {
                   "id":1,
                   "firstName":"firstName",
                   "lastName":"lastName",
                   "universityId":3,
                   "email":"example@gmail.com",
                   "phoneNumber":"+48 123 456 789",
                   "pictureUrl":"pictureUrl",
                   "facebookUrl":"facebookUrl",
                   "whatsUpUrl":"whatsUpUrl",
                   "instagramUsername":"instagramUserName"
                 }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(studentService.updateStudent(any(StudentRequestDto.class), eq(STUDENT_ID_1)))
                .thenReturn(Mono.just(studentResponseDto));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}")
                             .build(STUDENT_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(studentRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateStudent_shouldReturnBadRequestWhenMissingField() {
        // given
        var studentRequestDto = createStudentRequestDto().firstName(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;firstName.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(studentService.updateStudent(any(StudentRequestDto.class), eq(STUDENT_ID_1)))
                .thenThrow(new ConstraintViolationException(validator.validate(studentRequestDto)));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}")
                             .build(STUDENT_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(studentRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateStudent_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(studentService.updateStudent(any(StudentRequestDto.class), eq(STUDENT_ID_1)))
                .thenThrow(new IllegalStateException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}")
                             .build(STUDENT_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateStudent_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        var studentRequestDto = createStudentRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "student.not.found");
        when(studentService.updateStudent(any(StudentRequestDto.class), eq(STUDENT_ID_1)))
                .thenReturn(Mono.error(new NoSuchElementException("student")));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}")
                             .build(STUDENT_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(studentRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateStudent_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var studentRequestDto = createStudentRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(studentService.updateStudent(any(StudentRequestDto.class), eq(STUDENT_ID_1)))
                .thenThrow(new RuntimeException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}")
                             .build(STUDENT_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(studentRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateStudentUniversity_shouldReturnUniversityWhenDataCorrect() {
        // given
        var studentUniversityUpdateDto = new StudentUniversityUpdateDto(UNIVERSITY_ID);
        var universityResponseDto = createUniversityResponseDto();
        String expectedPayload = """
                {
                   "id":3,
                   "name":"name",
                   "address":"address",
                   "address2":"address2",
                   "zipcode":"00-000",
                   "city":"city",
                   "country":"country",
                   "description":"description",
                   "websiteUrl":"websiteUrl",
                   "logoUrl":"logoUrl"
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(studentService.updateStudentUniversity(any(StudentUniversityUpdateDto.class), eq(STUDENT_ID_1)))
                .thenReturn(Mono.just(universityResponseDto));

        // when-then
        webTestClient.patch()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/university")
                             .build(STUDENT_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(studentUniversityUpdateDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateStudentUniversity_shouldReturnBadRequestWhenMissingField() {
        // given
        var studentUniversityUpdateDto = new StudentUniversityUpdateDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;universityId.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(studentService.updateStudentUniversity(any(StudentUniversityUpdateDto.class), eq(STUDENT_ID_1)))
                .thenThrow(new ConstraintViolationException(validator.validate(studentUniversityUpdateDto)));

        // when-then
        webTestClient.patch()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/university")
                             .build(STUDENT_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(studentUniversityUpdateDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateStudentUniversity_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(studentService.updateStudentUniversity(any(StudentUniversityUpdateDto.class), eq(STUDENT_ID_1)))
                .thenThrow(new IllegalStateException());

        // when-then
        webTestClient.patch()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/university")
                             .build(STUDENT_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateStudentUniversity_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        var studentUniversityUpdateDto = new StudentUniversityUpdateDto(UNIVERSITY_ID);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "university.not.found");
        when(studentService.updateStudentUniversity(any(StudentUniversityUpdateDto.class), eq(STUDENT_ID_1)))
                .thenReturn(Mono.error(new NoSuchElementException("university")));

        // when-then
        webTestClient.patch()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/university")
                             .build(STUDENT_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(studentUniversityUpdateDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateStudentUniversity_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var studentUniversityUpdateDto = new StudentUniversityUpdateDto(UNIVERSITY_ID);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(studentService.updateStudentUniversity(any(StudentUniversityUpdateDto.class), eq(STUDENT_ID_1)))
                .thenThrow(new RuntimeException());

        // when-then
        webTestClient.patch()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/university")
                             .build(STUDENT_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(studentUniversityUpdateDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateStudentPhoto_shouldUpdateStudentPhotoWhenDataCorrect() {
        // given
        var multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource("example_file.txt")).contentType(MediaType.MULTIPART_FORM_DATA);
        var studentResponseDto = createStudentResponseDto();
        String expectedPayload = """
                {
                   "id":1,
                   "firstName":"firstName",
                   "lastName":"lastName",
                   "universityId":3,
                   "email":"example@gmail.com",
                   "phoneNumber":"+48 123 456 789",
                   "pictureUrl":"pictureUrl",
                   "facebookUrl":"facebookUrl",
                   "whatsUpUrl":"whatsUpUrl",
                   "instagramUsername":"instagramUserName"
                 }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(studentService.updateStudentPhoto(eq(STUDENT_ID_1), any(FilePart.class)))
                .thenReturn(Mono.just(studentResponseDto));

        // when-then
        webTestClient.patch()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/photo")
                             .build(STUDENT_ID_1))
                     .contentType(MediaType.MULTIPART_FORM_DATA)
                     .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateStudentPhoto_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(studentService.updateStudentPhoto(eq(STUDENT_ID_1), any(FilePart.class)))
                .thenThrow(new IllegalArgumentException());

        // when-then
        webTestClient.patch()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/photo")
                             .build(STUDENT_ID_1))
                     .contentType(MediaType.MULTIPART_FORM_DATA)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateStudentPhoto_shouldReturnBadRequestWhenWrongBodyFound() {
        // given
        var multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("wrong_field", new ClassPathResource("example_file.txt")).contentType(MediaType.MULTIPART_FORM_DATA);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(studentService.updateStudentPhoto(eq(STUDENT_ID_1), any(FilePart.class)))
                .thenThrow(new IllegalArgumentException());

        // when-then
        webTestClient.patch()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/photo")
                             .build(STUDENT_ID_1))
                     .contentType(MediaType.MULTIPART_FORM_DATA)
                     .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateStudentPhoto_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        var multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource("example_file.txt")).contentType(MediaType.MULTIPART_FORM_DATA);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "student.not.found");
        when(studentService.updateStudentPhoto(eq(STUDENT_ID_1), any(FilePart.class)))
                .thenReturn(Mono.error(new NoSuchElementException("student")));

        // when-then
        webTestClient.patch()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/photo")
                             .build(STUDENT_ID_1))
                     .contentType(MediaType.MULTIPART_FORM_DATA)
                     .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateStudentPhoto_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource("example_file.txt")).contentType(MediaType.MULTIPART_FORM_DATA);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(studentService.updateStudentPhoto(eq(STUDENT_ID_1), any(FilePart.class)))
                .thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.patch()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/photo")
                             .build(STUDENT_ID_1))
                     .contentType(MediaType.MULTIPART_FORM_DATA)
                     .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    private StudentListDto createStudentListDto(int id) {
        return StudentListDto.builder()
                             .id(id)
                             .firstName("firstName")
                             .lastName("lastName")
                             .pictureUrl("pictureUrl")
                             .build();
    }

    private StudentResponseDto createStudentResponseDto() {
        return StudentResponseDto.builder()
                                 .id(STUDENT_ID_1)
                                 .firstName("firstName")
                                 .lastName("lastName")
                                 .email("example@gmail.com")
                                 .universityId(UNIVERSITY_ID)
                                 .phoneNumber("+48 123 456 789")
                                 .facebookUrl("facebookUrl")
                                 .whatsUpUrl("whatsUpUrl")
                                 .instagramUsername("instagramUserName")
                                 .pictureUrl("pictureUrl")
                                 .build();
    }

    private StudentRequestDto createStudentRequestDto() {
        return StudentRequestDto.builder()
                                .firstName("firstName")
                                .lastName("lastName")
                                .email("example@gmail.com")
                                .phoneNumber("+48 123 456 789")
                                .universityId(UNIVERSITY_ID)
                                .facebookUrl("facebookUrl")
                                .whatsUpUrl("whatsUpUrl")
                                .instagramUsername("instagramUserName")
                                .build();
    }

    private UniversityResponseDto createUniversityResponseDto() {
        return UniversityResponseDto.builder()
                                    .id(UNIVERSITY_ID)
                                    .name("name")
                                    .address("address")
                                    .address2("address2")
                                    .city("city")
                                    .country("country")
                                    .zipcode("00-000")
                                    .description("description")
                                    .websiteUrl("websiteUrl")
                                    .logoUrl("logoUrl")
                                    .build();
    }
}

