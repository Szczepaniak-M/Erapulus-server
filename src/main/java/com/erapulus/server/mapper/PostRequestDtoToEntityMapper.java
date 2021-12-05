package com.erapulus.server.mapper;

import com.erapulus.server.database.model.PostEntity;
import com.erapulus.server.dto.PostRequestDto;
import org.springframework.stereotype.Component;

@Component
public class PostRequestDtoToEntityMapper implements RequestDtoToEntityMapper<PostRequestDto, PostEntity> {
    public PostEntity from(PostRequestDto postRequestDto) {
        return PostEntity.builder()
                         .title(postRequestDto.title())
                         .date(postRequestDto.date())
                         .content(postRequestDto.content())
                         .build();
    }
}
