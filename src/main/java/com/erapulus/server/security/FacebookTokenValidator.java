package com.erapulus.server.security;

import com.erapulus.server.database.model.StudentEntity;
import com.erapulus.server.dto.FacebookRegisterDto;
import com.erapulus.server.dto.StudentLoginDTO;
import com.erapulus.server.service.exception.InvalidTokenException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class FacebookTokenValidator {

    private final WebClient webClient;

    public FacebookTokenValidator(String facebookGraphApiBase) {
        this.webClient = WebClient.builder().baseUrl(facebookGraphApiBase).build();
    }

    public Mono<StudentEntity> validate(StudentLoginDTO studentLoginDTO) {
        return getUserDataFromFacebook(studentLoginDTO)
                .onErrorMap(WebClientResponseException.BadRequest.class, e -> new InvalidTokenException())
                .map(payload -> StudentEntity.builder()
                                          .email(payload.email())
                                          .firstName(payload.firstName())
                                          .lastName(payload.lastName())
                                          .pictureUrl(payload.pictureUrl())
                                          .build());
    }

    private Mono<FacebookRegisterDto> getUserDataFromFacebook(StudentLoginDTO studentLoginDTO) {
        String path = "/me?fields={fields}&redirect={redirect}&access_token={access_token}";
        String fields = "email,first_name,last_name,picture.width(720).height(720)";
        Map<String, String> variables = new HashMap<>();
        variables.put("fields", fields);
        variables.put("redirect", "false");
        variables.put("access_token", studentLoginDTO.token());
        return this.webClient.get()
                             .uri(path, variables)
                             .retrieve()
                             .bodyToMono(FacebookRegisterDto.class);
    }
}
