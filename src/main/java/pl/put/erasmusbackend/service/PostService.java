package pl.put.erasmusbackend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.put.erasmusbackend.database.model.PostEntity;
import pl.put.erasmusbackend.database.repository.PostRepository;
import pl.put.erasmusbackend.dto.PostRequestDto;
import pl.put.erasmusbackend.dto.PostResponseDto;
import pl.put.erasmusbackend.mapper.EntityToResponseDtoMapper;
import pl.put.erasmusbackend.mapper.RequestDtoToEntityMapper;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;


@Service
@Validated
public class PostService extends CrudGenericService<PostEntity, PostRequestDto, PostResponseDto> {

    private static final LocalDate MIN_DATE = LocalDate.of(1, 1, 1);
    private static final LocalDate MAX_DATE = LocalDate.of(9999, 12, 31);
    private static final Pattern PATTERN = Pattern.compile("^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$");
    private final PostRepository postRepository;

    public PostService(PostRepository repository,
                       RequestDtoToEntityMapper<PostRequestDto, PostEntity> requestDtoToEntityMapper,
                       EntityToResponseDtoMapper<PostEntity, PostResponseDto> entityToResponseDtoMapper) {
        super(repository, requestDtoToEntityMapper, entityToResponseDtoMapper);
        this.postRepository = repository;
    }

    public Mono<Page<PostResponseDto>> listPostByUniversityId(Integer universityId, String title, String fromDate, String toDate, PageRequest pageRequest) {
        return convertDate(fromDate, MIN_DATE)
                .zipWith(convertDate(toDate, MAX_DATE))
                .flatMap(fromAndToDate -> postRepository.findPostByFilters(universityId, title, fromAndToDate.getT1(), fromAndToDate.getT2(), pageRequest.getOffset(), pageRequest.getPageSize())
                                                        .map(entityToResponseDtoMapper::from)
                                                        .collectList()
                                                        .zipWith(postRepository.countPostByFilters(universityId, title, fromAndToDate.getT1(), fromAndToDate.getT2()))
                                                        .map(t -> new PageImpl<>(t.getT1(), pageRequest, t.getT2())));

    }

    public Mono<PostResponseDto> createEntity(PostRequestDto requestDto, int universityId) {
        UnaryOperator<PostEntity> addParamFromPath = postEntity -> postEntity.universityId(universityId);
        return createEntity(requestDto, addParamFromPath);
    }

    public Mono<PostResponseDto> updateEntity(PostRequestDto requestDto, int postId, int universityId) {
        UnaryOperator<PostEntity> addParamFromPath = postEntity -> postEntity.id(postId).universityId(universityId);
        return updateEntity(requestDto, addParamFromPath);
    }

    private Mono<LocalDate> convertDate(String date, LocalDate defaultValue) {
        if (date.equals("")) {
            return Mono.just(defaultValue);
        } else if (PATTERN.matcher(date).matches()) {
            Integer[] dateSplit = Arrays.stream(date.split("-"))
                                        .map(Integer::parseInt)
                                        .toArray(Integer[]::new);
            return Mono.just(LocalDate.of(dateSplit[0], dateSplit[1], dateSplit[2]));
        } else {
            return Mono.error(new IllegalArgumentException());
        }
    }


}
