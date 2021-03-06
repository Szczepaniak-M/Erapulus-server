package com.erapulus.server.common.web;

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

    // ApplicationUser
    public static final String USER_BASE_URL_OPENAPI = "/api/user";
    public static final String USER_DETAILS_URL_OPENAPI = "/api/user/{userId}";

    // Student
    public static final String STUDENT_BASE_URL_OPENAPI = "/api/student";
    public static final String STUDENT_DETAILS_URL_OPENAPI = "/api/student/{studentId}";
    public static final String STUDENT_UPDATE_UNIVERSITY_URL_OPENAPI = "/api/student/{studentId}/university";
    public static final String STUDENT_UPDATE_PHOTO_URL_OPENAPI = "/api/student/{studentId}/photo";

    // Employee
    public static final String EMPLOYEE_LIST_URL_OPENAPI = "/api/university/{universityId}/employee";
    public static final String EMPLOYEE_DETAILS_URL_OPENAPI = "/api/employee/{employeeId}";

    // University
    public static final String UNIVERSITY_BASE_URL_OPENAPI = "/api/university";
    public static final String UNIVERSITY_DETAILS_URL_OPENAPI = "/api/university/{universityId}";
    public static final String UNIVERSITY_UPDATE_LOGO_URL_OPENAPI = "/api/university/{universityId}/logo";


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

    // Document
    public static final String DOCUMENT_UNIVERSITY_BASE_URL_OPENAPI = "/api/university/{universityId}/document";
    public static final String DOCUMENT_UNIVERSITY_DETAILS_URL_OPENAPI = "/api/university/{universityId}/document/{documentId}";
    public static final String DOCUMENT_PROGRAM_BASE_URL_OPENAPI = "/api/university/{universityId}/faculty/{facultyId}/program/{programId}/document";
    public static final String DOCUMENT_PROGRAM_DETAILS_URL_OPENAPI = "/api/university/{universityId}/faculty/{facultyId}/program/{programId}/document/{documentId}";
    public static final String DOCUMENT_MODULE_BASE_URL_OPENAPI = "/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module/{moduleId}/document";
    public static final String DOCUMENT_MODULE_DETAILS_URL_OPENAPI = "/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module/{moduleId}/document/{documentId}";

    // Friend
    public static final String FRIEND_BASE_URL_OPENAPI = "/api/student/{studentId}/friend";
    public static final String FRIEND_DETAILS_URL_OPENAPI = "/api/student/{studentId}/friend/{friendId}";
    public static final String FRIEND_SEARCH_URL_OPENAPI = "/api/student/{studentId}/friend/search";
    public static final String FRIEND_REQUESTS_URL_OPENAPI = "/api/student/{studentId}/friend/request";
}
