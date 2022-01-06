package com.erapulus.server.post.mapper;

import com.erapulus.server.TestUtils;
import com.erapulus.server.post.database.PostEntity;
import com.erapulus.server.post.dto.PostRequestDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PostRequestDtoToEntityMapperTest {

    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final LocalDate DATE = LocalDate.now();

    @Test
    void from_shouldMapDtoToEntity() {
        // given
        PostRequestDto requestDto = PostRequestDto.builder()
                                                  .title(TITLE)
                                                  .content(CONTENT)
                                                  .build();

        // when
        PostEntity result = new PostRequestDtoToEntityMapper().from(requestDto);

        //then
        assertNull(result.id());
        assertEquals(TITLE, result.title());
        assertEquals(CONTENT, result.content());
        assertNull(result.date());
        assertNull(result.universityId());
        assertThat(TestUtils.getValidationResult(result)).containsExactlyInAnyOrderElementsOf(List.of("id", "universityId", "date"));
    }
}