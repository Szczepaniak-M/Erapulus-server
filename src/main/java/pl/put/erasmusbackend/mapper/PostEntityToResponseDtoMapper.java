package pl.put.erasmusbackend.mapper;

import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.database.model.PostEntity;
import pl.put.erasmusbackend.dto.PostResponseDto;

@Component
public class PostEntityToResponseDtoMapper implements EntityToResponseDtoMapper<PostEntity, PostResponseDto> {
    public PostResponseDto from(PostEntity postEntity) {
        return PostResponseDto.builder()
                              .id(postEntity.id())
                              .title(postEntity.title())
                              .date(postEntity.date())
                              .content(postEntity.content())
                              .build();
    }

}
