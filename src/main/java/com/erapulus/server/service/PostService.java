package com.erapulus.server.service;

import com.erapulus.server.database.model.PostEntity;
import com.erapulus.server.database.repository.PostRepository;
import com.erapulus.server.database.repository.UniversityRepository;
import com.erapulus.server.dto.PostRequestDto;
import com.erapulus.server.dto.PostResponseDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.web.common.PageablePayload;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;


@Service
@Validated
public class PostService extends CrudGenericService<PostEntity, PostRequestDto, PostResponseDto> {

    private static final LocalDate MIN_DATE = LocalDate.of(1, 1, 1);
    private static final LocalDate MAX_DATE = LocalDate.of(9999, 12, 31);
    private static final Pattern PATTERN = Pattern.compile("^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$");
    private final PostRepository postRepository;
    private final UniversityRepository universityRepository;

    public PostService(PostRepository repository,
                       RequestDtoToEntityMapper<PostRequestDto, PostEntity> requestDtoToEntityMapper,
                       EntityToResponseDtoMapper<PostEntity, PostResponseDto> entityToResponseDtoMapper,
                       UniversityRepository universityRepository) {
        super(repository, requestDtoToEntityMapper, entityToResponseDtoMapper, "post");
        this.postRepository = repository;
        this.universityRepository = universityRepository;
    }

    public Mono<PageablePayload<PostResponseDto>> listEntities(Integer universityId, String title, String fromDate, String toDate, PageRequest pageRequest) {
        return universityRepository.existsById(universityId)
                                   .switchIfEmpty(Mono.error(new NoSuchElementException("university")))
                                   .then(convertDate(fromDate, MIN_DATE))
                                   .zipWith(convertDate(toDate, MAX_DATE))
                                   .flatMap(fromAndToDate -> postRepository.findPostByFilters(universityId, title, fromAndToDate.getT1(), fromAndToDate.getT2(), pageRequest.getOffset(), pageRequest.getPageSize())
                                                                           .map(entityToResponseDtoMapper::from)
                                                                           .collectList()
                                                                           .zipWith(postRepository.countPostByFilters(universityId, title, fromAndToDate.getT1(), fromAndToDate.getT2()))
                                                                           .map(dtoListAndTotalCount -> new PageablePayload<>(dtoListAndTotalCount.getT1(), pageRequest, dtoListAndTotalCount.getT2())));

    }

    public Mono<PostResponseDto> createEntity(@Valid PostRequestDto requestDto, int universityId) {
        UnaryOperator<PostEntity> addParamFromPath = postEntity -> postEntity.universityId(universityId);
        return createEntity(requestDto, addParamFromPath);
    }

    public Mono<PostResponseDto> getEntityById(int postId, int universityId) {
        Supplier<Mono<PostEntity>> supplier = () -> postRepository.findByIdAndUniversityId(postId, universityId);
        return getEntityById(supplier);
    }

    public Mono<PostResponseDto> updateEntity(@Valid PostRequestDto requestDto, int postId, int universityId) {
        UnaryOperator<PostEntity> addParamFromPath = post -> post.id(postId).universityId(universityId);
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
