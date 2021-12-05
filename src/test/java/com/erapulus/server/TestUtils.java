package com.erapulus.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {

    public static String parseToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public static void assertJsonEquals(String expected, String actual) {
        String expectedWithoutNewLine = expected.replaceAll("\n", "");
        try {
            JSONAssert.assertEquals(expectedWithoutNewLine, actual, JSONCompareMode.STRICT);
        } catch (JSONException e) {
            fail(e.getMessage());
        }
    }

    public static String getBodyAsString(EntityExchangeResult<byte[]> body) {
        return new String(Optional.ofNullable(body.getResponseBody()).orElse("".getBytes()));
    }

}
