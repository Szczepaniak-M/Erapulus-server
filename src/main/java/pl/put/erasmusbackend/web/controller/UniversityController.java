package pl.put.erasmusbackend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.put.erasmusbackend.database.model.University;
import pl.put.erasmusbackend.database.repository.UniversityRepository;
import reactor.core.publisher.Mono;


@Controller
@AllArgsConstructor
public class UniversityController {

    private final UniversityRepository universityRepository;

    @Operation(
            operationId = "list-universities",
            tags = "University",
            description = "List universities",
            summary = "List universities",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))
            }
    )
    public Mono<ServerResponse> getUniversityList(ServerRequest request) {
        return universityRepository.findAll()
                                   .map(University::name)
                                   .collectList()
                                   .flatMap(universityNames -> ServerResponse.ok()
                                                                             .contentType(MediaType.APPLICATION_JSON)
                                                                             .body(BodyInserters.fromValue(universityNames)));
    }
}
