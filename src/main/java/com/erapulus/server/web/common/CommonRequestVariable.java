package com.erapulus.server.web.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonRequestVariable {
    public static final String UNIVERSITY_PATH_PARAM = "universityId";
    public static final String POST_PATH_PARAM = "postId";
    public static final String BUILDING_PATH_PARAM = "buildingId";
    public static final String FACULTY_PATH_PARAM = "facultyId";
    public static final String PROGRAM_PATH_PARAM = "programId";
    public static final String MODULE_PATH_PARAM = "moduleId";
    public static final String USER_PATH_PARAM = "userId";
    public static final String STUDENT_PATH_PARAM = "studentId";
    public static final String EMPLOYEE_PATH_PARAM = "employeeId";
    public static final String DEVICE_PATH_PARAM = "deviceId";
    public static final String TITLE_QUERY_PARAM = "title";
    public static final String FROM_QUERY_PARAM = "from";
    public static final String TO_QUERY_PARAM = "to";
    public static final String UNIVERSITY_QUERY_PARAM = "university";
    public static final String TYPE_QUERY_PARAM = "type";
    public static final String NAME_QUERY_PARAM = "name";
    public static final String EMAIL_QUERY_PARAM = "email";
    public static final String PAGE_QUERY_PARAM = "page";
    public static final String PAGE_SIZE_QUERY_PARAM = "pageSize";
}
