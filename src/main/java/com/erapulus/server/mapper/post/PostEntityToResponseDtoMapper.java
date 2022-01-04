package com.erapulus.server.mapper.post;

import com.erapulus.server.database.model.PostEntity;
import com.erapulus.server.dto.post.PostResponseDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class PostEntityToResponseDtoMapper implements EntityToResponseDtoMapper<PostEntity, PostResponseDto> {
    public PostResponseDto from(PostEntity postEntity) {
        return PostResponseDto.builder()
                              .id(postEntity.id())
                              .title(postEntity.title())
                              .date(postEntity.date())
                              .content(postEntity.content())
                              .universityId(postEntity.universityId())
                              .build();
    }

}