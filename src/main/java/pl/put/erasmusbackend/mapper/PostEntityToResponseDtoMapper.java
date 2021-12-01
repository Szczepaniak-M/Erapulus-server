package pl.put.erasmusbackend.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.put.erasmusbackend.database.model.PostEntity;
import pl.put.erasmusbackend.dto.PostResponseDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostEntityToResponseDtoMapper {
    public static PostResponseDto from(PostEntity postEntity) {
        return PostResponseDto.builder()
                              .id(postEntity.id())
                              .title(postEntity.title())
                              .date(postEntity.date())
                              .content(postEntity.content())
                              .build();
    }

}
