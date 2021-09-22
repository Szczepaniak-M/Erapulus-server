package pl.put.erasmusbackend.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.put.erasmusbackend.TestUtils;
import pl.put.erasmusbackend.database.model.University;
import pl.put.erasmusbackend.database.repository.UniversityRepository;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

@SpringBootTest
class UniversityControllerTest {

    @Autowired
    ApplicationContext applicationContext;

    @MockBean
    UniversityRepository universityRepository;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    void getUniversityList_shouldReturnUniversityNames() {
        var expected = "[\"university1\", \"university2\"]";
        var university1 = new University().name("university1");
        var university2 = new University().name("university2");
        when(universityRepository.findAll()).thenReturn(Flux.just(university1, university2));
        webTestClient.get()
                     .uri("/university")
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(TestUtils.getBodyAsString(body), expected));
    }
}
