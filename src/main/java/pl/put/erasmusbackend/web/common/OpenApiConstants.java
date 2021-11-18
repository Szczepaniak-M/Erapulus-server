package pl.put.erasmusbackend.web.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenApiConstants {
    // Status codes
    public static final String OK = "OK";
    public static final String CREATED = "CREATED";
    public static final String NO_CONTENT = "NO CONTENT";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String BAD_REQUEST = "BAD REQUEST";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String NOT_FOUND = "NOT FOUND";
    public static final String CONFLICT = "CONFLICT";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL SERVER ERROR";

    // Endpoints
    public static final String BUILDING_BASE_URL_OPENAPI = "api/university/{universityId}/building";
    public static final String BUILDING_DETAILS_URL_OPENAPI = "api/university/{universityId}/building/{buildingId}";
}
