package com.erapulus.server.security;

import com.erapulus.server.dto.StudentLoginDTO;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.erapulus.server.database.model.StudentEntity;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class GoogleTokenValidator {

    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    public Mono<StudentEntity> validate(StudentLoginDTO studentLoginDTO) {

        return Mono.fromCallable(() -> googleIdTokenVerifier.verify(studentLoginDTO.token()))
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
