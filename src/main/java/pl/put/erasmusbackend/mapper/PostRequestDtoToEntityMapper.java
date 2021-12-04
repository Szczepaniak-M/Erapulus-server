package pl.put.erasmusbackend.mapper;

import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.database.model.PostEntity;
import pl.put.erasmusbackend.dto.PostRequestDto;

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
