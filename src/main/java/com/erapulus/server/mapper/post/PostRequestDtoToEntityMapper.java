package com.erapulus.server.mapper.post;

import com.erapulus.server.database.model.PostEntity;
import com.erapulus.server.dto.post.PostRequestDto;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class PostRequestDtoToEntityMapper implements RequestDtoToEntityMapper<PostRequestDto, PostEntity> {
    public PostEntity from(PostRequestDto postRequestDto) {
        return PostEntity.builder()
                         .title(postRequestDto.title())
                         .content(postRequestDto.content())
                         .build();
    }
}
