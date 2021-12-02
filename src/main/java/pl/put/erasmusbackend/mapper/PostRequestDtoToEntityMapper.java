package pl.put.erasmusbackend.mapper;

import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.database.model.PostEntity;
import pl.put.erasmusbackend.dto.PostRequestDto;

@Component
public class PostRequestDtoToEntityMapper implements RequestDtoToEntityMapper<PostRequestDto, PostEntity> {
    public PostEntity from(PostRequestDto postEntity) {
        return PostEntity.builder()
                         .title(postEntity.title())
                         .date(postEntity.date())
                         .content(postEntity.content())
                         .build();
    }
}
