package com.erapulus.server.web.controller;

import com.erapulus.server.dto.UniversityListDto;
import com.erapulus.server.web.router.UniversityRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.erapulus.server.TestUtils;
import com.erapulus.server.service.UniversityService;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {UniversityRouter.class, UniversityController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class UniversityControllerTest {

    private static final int UNIVERSITY_ID_1 = 1;
    private static final int UNIVERSITY_ID_2 = 2;
    private static final String UNIVERSITY_NAME_1 = "university1";
    private static final String UNIVERSITY_NAME_2 = "university2";
    private static final String LOGO_URL_1 = "logoUrl1";
    private static final String LOGO_URL_2 = "logoUrl2";

    @Autowired
    ApplicationContext applicationContext;

    @MockBean
    UniversityService universityService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    void getUniversityList_shouldReturnUniversityNames() {
        // given
        var expected = """
                {
                  "status": 200,
                  "payload": [
                    {
                      "id": 1,
                      "name": "university1",
                      "logoUrl": "logoUrl1"
                    },
                    {
                      "id": 2,
                      "name": "university2",
                      "logoUrl": "logoUrl2"
                    }
                  ],
                  "message": null
                }""";
        var university1 = UniversityListDto.builder().name(UNIVERSITY_NAME_1).id(UNIVERSITY_ID_1).logoUrl(LOGO_URL_1).build();
        var university2 = UniversityListDto.builder().name(UNIVERSITY_NAME_2).id(UNIVERSITY_ID_2).logoUrl(LOGO_URL_2).build();
        when(universityService.listEntities()).thenReturn(Mono.just(List.of(university1, university2)));

        // when-then
        webTestClient.get()
                     .uri("/api/university")
                     .header("Content-Type", MediaType.APPLICATION_JSON.toString())
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expected, TestUtils.getBodyAsString(body)));
    }
}
