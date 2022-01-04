package com.erapulus.server.mapper.post;

import com.erapulus.server.database.model.PostEntity;
import com.erapulus.server.dto.post.PostRequestDto;
import com.erapulus.server.mapper.ValidatorUtils;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
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
        assertThat(ValidatorUtils.getValidationResult(result)).containsExactlyInAnyOrderElementsOf(List.of("id", "universityId", "date"));
    }
}