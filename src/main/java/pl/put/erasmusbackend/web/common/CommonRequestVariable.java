package pl.put.erasmusbackend.web.common;

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
    public static final String TITLE_QUERY_PARAM = "title";
    public static final String FROM_QUERY_PARAM = "from";
    public static final String TO_QUERY_PARAM = "to";
    public static final String PAGE_QUERY_PARAM = "page";
    public static final String PAGE_SIZE_QUERY_PARAM = "pageSize";
}
