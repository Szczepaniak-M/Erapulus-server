package pl.put.erasmusbackend.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.put.erasmusbackend.TestUtils;
import pl.put.erasmusbackend.database.model.UniversityEntity;
import pl.put.erasmusbackend.database.repository.UniversityRepository;
import pl.put.erasmusbackend.web.router.UniversityRouter;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {UniversityRouter.class, UniversityController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
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
        // given
        var expected = "[\"university1\", \"university2\"]";
        var university1 = new UniversityEntity().name("university1");
        var university2 = new UniversityEntity().name("university2");
        when(universityRepository.findAll()).thenReturn(Flux.just(university1, university2));

        // when-then
        webTestClient.get()
                     .uri("/university")
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(TestUtils.getBodyAsString(body), expected));
    }
}
