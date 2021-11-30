package pl.put.erasmusbackend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pl.put.erasmusbackend.database.repository.UniversityRepository;
import pl.put.erasmusbackend.dto.UniversityListDto;
import pl.put.erasmusbackend.dto.UniversityRequestDto;
import pl.put.erasmusbackend.dto.UniversityResponseDto;
import pl.put.erasmusbackend.mapper.UniversityEntityToResponseDtoMapper;
import pl.put.erasmusbackend.mapper.UniversityEntityToUniversityListDtoMapper;
import pl.put.erasmusbackend.mapper.UniversityRequestDtoToEntityMapper;
import pl.put.erasmusbackend.service.exception.NoSuchBuildingException;
import pl.put.erasmusbackend.service.exception.NoSuchUniversityException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@Service
@Validated
@AllArgsConstructor
public class UniversityService {

    private final UniversityRepository universityRepository;

    public Mono<List<UniversityListDto>> listUniversities() {
        return universityRepository.findAllUniversitiesNameAndId()
                                   .map(UniversityEntityToUniversityListDtoMapper::from)
                                   .collectList();
    }

    public Mono<UniversityResponseDto> createUniversity(@Valid UniversityRequestDto universityRequestDto) {
        return Mono.just(universityRequestDto)
                   .map(UniversityRequestDtoToEntityMapper::from)
                   .flatMap(universityRepository::save)
                   .map(UniversityEntityToResponseDtoMapper::from);
    }

    public Mono<UniversityResponseDto> getUniversityById(int universityId) {
        return universityRepository.findById(universityId)
                                   .map(UniversityEntityToResponseDtoMapper::from);
    }

    public Mono<UniversityResponseDto> updateUniversity(int universityId, @Valid UniversityRequestDto universityRequestDto) {
        return Mono.just(universityRequestDto)
                   .map(UniversityRequestDtoToEntityMapper::from)
                   .map(university -> university.id(universityId))
                   .flatMap(updatedUniversity -> universityRepository.findById(updatedUniversity.id())
                                                                     .switchIfEmpty(Mono.error(new NoSuchUniversityException()))
                                                                     .flatMap(b -> universityRepository.save(updatedUniversity)))
                   .map(UniversityEntityToResponseDtoMapper::from);
    }

    @Transactional
    public Mono<Boolean> deleteUniversity(int universityId) {
        // TODO Add deleting Posts, Building, Documents, Programs and Modules,
        return universityRepository.findById(universityId)
                                   .switchIfEmpty(Mono.error(new NoSuchBuildingException()))
                                   .flatMap(b -> universityRepository.deleteById(universityId))
                                   .thenReturn(true);
    }
}
