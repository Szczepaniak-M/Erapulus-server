package com.erapulus.server.post.mapper;

import com.erapulus.server.post.database.PostEntity;
import com.erapulus.server.post.dto.PostRequestDto;
import com.erapulus.server.common.mapper.RequestDtoToEntityMapper;
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
