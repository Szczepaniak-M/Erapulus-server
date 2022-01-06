package com.erapulus.server.common.service;

import com.erapulus.server.common.database.UserType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryParamParser {

    public static Integer parseInteger(String integer) {
        Integer value = null;
        if (!Objects.equals(integer, "")) {
            value = Integer.parseInt(integer);
        }
        return value;
    }

    public static UserType parseUseType(String userType) {
        UserType value = null;
        if (!Objects.equals(userType, "")) {
            value = UserType.valueOf(userType);
        }
        return value;
    }

    public static String parseString(String string) {
        String value = null;
        if (!Objects.equals(string, "")) {
            value = string;
        }
        return value;
    }
}
