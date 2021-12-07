package com.erapulus.server.web.common;

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

    // University
    public static final String UNIVERSITY_BASE_URL_OPENAPI = "/api/university";
    public static final String UNIVERSITY_DETAILS_URL_OPENAPI = "/api/university/{universityId}";

    // Building
    public static final String BUILDING_BASE_URL_OPENAPI = "/api/university/{universityId}/building";
    public static final String BUILDING_DETAILS_URL_OPENAPI = "/api/university/{universityId}/building/{buildingId}";

    // Post
    public static final String POST_BASE_URL_OPENAPI = "/api/university/{universityId}/post";
    public static final String POST_DETAILS_URL_OPENAPI = "/api/university/{universityId}/post/{postId}";

    // Faculty
    public static final String FACULTY_BASE_URL_OPENAPI = "/api/university/{universityId}/faculty";
    public static final String FACULTY_DETAILS_URL_OPENAPI = "/api/university/{universityId}/faculty/{facultyId}";

    // Program
    public static final String PROGRAM_BASE_URL_OPENAPI = "/api/university/{universityId}/faculty/{facultyId}/program";
    public static final String PROGRAM_DETAILS_URL_OPENAPI = "/api/university/{universityId}/faculty/{facultyId}/program/{programId}";

    // Module
    public static final String MODULE_BASE_URL_OPENAPI = "/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module";
    public static final String MODULE_DETAILS_URL_OPENAPI = "/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module/{moduleId}";

    // Device
    public static final String DEVICE_BASE_URL_OPENAPI = "/api/student/{studentId}/device";
    public static final String DEVICE_DETAILS_URL_OPENAPI = "/api/student/{studentId}/device/{deviceId}";
}
