package com.erapulus.server.common.service;

import com.erapulus.server.common.database.UserType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryParamParserTest {

    @Test
    void parseInteger_shouldReturnIntWhenProperInput() {
        // given
        String input = "12";

        // when
        Integer result = QueryParamParser.parseInteger(input);

        // then
        assertEquals(12, result);
    }

    @Test
    void parseInteger_shouldReturnNullWhenEmptyString() {
        // given
        String input = "";

        // when
        Integer result = QueryParamParser.parseInteger(input);

        // then
        assertNull(result);
    }

    @Test
    void parseInteger_shouldThrowExceptionWhenCannotParseToInt() {
        // given
        String input = "12a";

        // when and then
        assertThrows(NumberFormatException.class, () -> {
            Integer result = QueryParamParser.parseInteger(input);
        });
    }

    @Test
    void parseUseType_shouldReturnUserTypeWhenProperInput() {
        // given
        String input = "STUDENT";

        // when
        UserType result = QueryParamParser.parseUseType(input);

        // then
        assertEquals(UserType.STUDENT, result);
    }

    @Test
    void parseUseType_shouldReturnNullWhenEmptyString() {
        // given
        String input = "";

        // when
        UserType result = QueryParamParser.parseUseType(input);

        // then
        assertNull(result);
    }

    @Test
    void parseUseType_shouldThrowExceptionWhenCannotParseToUserType() {
        // given
        String input = "STU";

        // when and then
        assertThrows(IllegalArgumentException.class, () -> {
            UserType result = QueryParamParser.parseUseType(input);
        });
    }

    @Test
    void parseString_shouldReturnStringWhenProperInput() {
        // given
        String input = "student";

        // when
        String result = QueryParamParser.parseString(input);

        // then
        assertEquals("student", result);
    }

    @Test
    void parseString_shouldReturnNullWhenEmptyString() {
        // given
        String input = "";

        // when
        String result = QueryParamParser.parseString(input);

        // then
        assertNull(result);
    }
}