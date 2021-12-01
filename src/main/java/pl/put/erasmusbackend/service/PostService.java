package pl.put.erasmusbackend.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.put.erasmusbackend.database.repository.PostRepository;
import pl.put.erasmusbackend.dto.PostRequestDto;
import pl.put.erasmusbackend.dto.PostResponseDto;
import pl.put.erasmusbackend.mapper.PostEntityToResponseDtoMapper;
import pl.put.erasmusbackend.mapper.PostRequestDtoToEntityMapper;
import pl.put.erasmusbackend.service.exception.NoSuchPostException;
import pl.put.erasmusbackend.service.exception.NoSuchProgramException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.regex.Pattern;


@Service
@Validated
@AllArgsConstructor
public class PostService {

    private static final LocalDate MIN_DATE = LocalDate.of(1, 1, 1);
    private static final LocalDate MAX_DATE = LocalDate.of(9999, 12, 31);
    private final PostRepository postRepository;
    private final Pattern pattern = Pattern.compile("^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$");

    public Mono<Page<PostResponseDto>> listPostByUniversityId(Integer universityId, String title, String fromDate, String toDate, PageRequest pageRequest) {
        return convertDate(fromDate, MIN_DATE)
                .zipWith(convertDate(toDate, MAX_DATE))
                .flatMap(fromAndToDate -> postRepository.findPostByFilters(universityId, title, fromAndToDate.getT1(), fromAndToDate.getT2(), pageRequest.getOffset(), pageRequest.getPageSize())
                                                        .map(PostEntityToResponseDtoMapper::from)
                                                        .collectList()
                                                        .zipWith(postRepository.countPostByFilters(universityId, title, fromAndToDate.getT1(), fromAndToDate.getT2()))
                                                        .map(t -> new PageImpl<>(t.getT1(), pageRequest, t.getT2())));

    }

    public Mono<PostResponseDto> createPost(Integer universityId, @Valid PostRequestDto postDto) {
        return Mono.just(postDto)
                   .map(PostRequestDtoToEntityMapper::from)
                   .map(post -> post.universityId(universityId))
                   .flatMap(postRepository::save)
                   .map(PostEntityToResponseDtoMapper::from);
    }

    public Mono<PostResponseDto> getPostById(Integer postId) {
        return postRepository.findById(postId)
                             .map(PostEntityToResponseDtoMapper::from)
                             .switchIfEmpty(Mono.error(new NoSuchProgramException()));
    }

    public Mono<PostResponseDto> updatePost(Integer universityId, Integer postId, @Valid PostRequestDto postDto) {
        return Mono.just(postDto)
                   .map(PostRequestDtoToEntityMapper::from)
                   .map(post -> post.id(postId)
                                    .universityId(universityId))
                   .flatMap(updatedPost -> postRepository.findById(updatedPost.id())
                                                         .switchIfEmpty(Mono.error(new NoSuchProgramException()))
                                                         .flatMap(b -> postRepository.save(updatedPost)))
                   .map(PostEntityToResponseDtoMapper::from);
    }

    public Mono<Boolean> deletePost(int postId) {
        return postRepository.findById(postId)
                             .switchIfEmpty(Mono.error(new NoSuchPostException()))
                             .flatMap(b -> postRepository.deleteById(postId))
                             .thenReturn(true);
    }

    private Mono<LocalDate> convertDate(String date, LocalDate defaultValue) {
        if (date.equals("")) {
            return Mono.just(defaultValue);
        } else if (pattern.matcher(date).matches()) {
            Integer[] dateSplit = Arrays.stream(date.split("-"))
                                        .map(Integer::parseInt)
                                        .toArray(Integer[]::new);
            return Mono.just(LocalDate.of(dateSplit[0], dateSplit[1], dateSplit[2]));
        } else {
            return Mono.error(new IllegalArgumentException());
        }
    }
}
