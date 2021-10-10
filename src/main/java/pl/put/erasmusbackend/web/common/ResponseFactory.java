package pl.put.erasmusbackend.web.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseFactory {

    public static <T> ResponseTemplate<T> createHttpSuccessResponse(T payload) {
        return ResponseTemplate.<T>builder()
                               .status(HttpStatus.OK.value())
                               .payload(payload)
                               .build();
    }

    public static <T> ResponseTemplate<T> createHttpCreatedResponse(T payload) {
        return ResponseTemplate.<T>builder()
                               .status(HttpStatus.CREATED.value())
                               .payload(payload)
                               .build();
    }

    public static ResponseTemplate<Object> createHttpNotFoundResponse(String objectName) {
        return ResponseTemplate.builder()
                               .status(HttpStatus.NOT_FOUND.value())
                               .message(objectName + ".not.found")
                               .build();
    }

    public static ResponseTemplate<Object> createHttpBadCredentialsResponse() {
        return ResponseTemplate.builder()
                               .status(HttpStatus.UNAUTHORIZED.value())
                               .message("bad.credentials")
                               .build();
    }
}
