package com.erapulus.server.security;

import com.erapulus.server.common.exception.InvalidTokenException;
import com.erapulus.server.student.database.StudentEntity;
import com.erapulus.server.student.dto.StudentLoginDTO;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class GoogleTokenValidator {

    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    public Mono<StudentEntity> validate(StudentLoginDTO studentLoginDTO) {
        return Mono.fromCallable(() -> googleIdTokenVerifier.verify(studentLoginDTO.token()))
                   .switchIfEmpty(Mono.error(new InvalidTokenException()))
                   .onErrorMap(IllegalArgumentException.class, e -> new InvalidTokenException())
                   .map(idToken -> {
                       GoogleIdToken.Payload payload = idToken.getPayload();
                       return StudentEntity.builder()
                                           .email(payload.getEmail())
                                           .firstName((String) payload.get("given_name"))
                                           .lastName((String) payload.get("family_name"))
                                           .pictureUrl((String) payload.get("picture"))
                                           .build();
                   });
    }
}
