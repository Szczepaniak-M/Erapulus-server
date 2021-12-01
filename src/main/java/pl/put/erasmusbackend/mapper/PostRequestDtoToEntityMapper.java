package pl.put.erasmusbackend.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.put.erasmusbackend.database.model.PostEntity;
import pl.put.erasmusbackend.dto.PostRequestDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostRequestDtoToEntityMapper {
    public static PostEntity from(PostRequestDto postEntity) {
        return PostEntity.builder()
                         .title(postEntity.title())
                         .date(postEntity.date())
                         .content(postEntity.content())
                         .build();
    }
}
