package pl.put.erasmusbackend.web.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenApiConstants {
    public static final String OK = "OK";
    public static final String CREATED = "CREATED";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String BAD_REQUEST = "BAD REQUEST";
    public static final String CONFLICT = "CONFLICT";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL SERVER ERROR";
}
