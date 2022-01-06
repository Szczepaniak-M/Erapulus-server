package com.erapulus.server.post.mapper;

import com.erapulus.server.post.database.PostEntity;
import com.erapulus.server.post.dto.PostResponseDto;
import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
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
