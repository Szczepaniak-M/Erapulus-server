package com.erapulus.server.service;

import com.erapulus.server.database.model.UniversityEntity;
import com.erapulus.server.database.repository.UniversityRepository;
import com.erapulus.server.dto.UniversityListDto;
import com.erapulus.server.dto.UniversityRequestDto;
import com.erapulus.server.dto.UniversityResponseDto;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.mapper.UniversityEntityToUniversityListDtoMapper;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@Service
@Validated
public class UniversityService extends CrudGenericService<UniversityEntity, UniversityRequestDto, UniversityResponseDto> {

    private final UniversityRepository universityRepository;
    private final UniversityEntityToUniversityListDtoMapper universityEntityToUniversityListDtoMapper;
    private final AzureStorageService azureStorageService;

    public UniversityService(UniversityRepository universityRepository,
                             RequestDtoToEntityMapper<UniversityRequestDto, UniversityEntity> requestDtoToEntityMapper,
                             EntityToResponseDtoMapper<UniversityEntity, UniversityResponseDto> entityToResponseDtoMapper,
                             UniversityEntityToUniversityListDtoMapper universityEntityToUniversityListDtoMapper,
                             AzureStorageService azureStorageService) {
        super(universityRepository, requestDtoToEntityMapper, entityToResponseDtoMapper);
        this.universityRepository = universityRepository;
        this.universityEntityToUniversityListDtoMapper = universityEntityToUniversityListDtoMapper;
        this.azureStorageService = azureStorageService;
    }

    public Mono<List<UniversityListDto>> listEntities() {
        return universityRepository.findAllUniversities()
                                   .map(universityEntityToUniversityListDtoMapper::from)
                                   .collectList();
    }

    public Mono<UniversityResponseDto> createEntity(@Valid UniversityRequestDto requestDto) {
        UnaryOperator<UniversityEntity> addParamFromPath = university -> university;
        return createEntity(requestDto, addParamFromPath);
    }

    public Mono<UniversityResponseDto> getEntityById(int universityId) {
        Supplier<Mono<UniversityEntity>> supplier = () -> universityRepository.findById(universityId);
        return getEntityById(supplier);
    }

    public Mono<UniversityResponseDto> updateEntity(@Valid UniversityRequestDto requestDto, int universityId) {
        return Mono.just(requestDto)
                   .map(requestDtoToEntityMapper::from)
                   .map(university -> university.id(universityId))
                   .flatMap(updatedUniversity -> universityRepository.findById(updatedUniversity.id())
                                                                     .switchIfEmpty(Mono.error(new NoSuchElementException()))
                                                                     .flatMap(oldEntity -> universityRepository.save(updatedUniversity.logoUrl(oldEntity.logoUrl()))))
                   .map(entityToResponseDtoMapper::from);
    }

    @Override
    @Transactional
    public Mono<Boolean> deleteEntity(int universityId) {
        // TODO Add deleting Posts, Building, Documents, Programs and Modules,
        return universityRepository.findById(universityId)
                                   .switchIfEmpty(Mono.error(new NoSuchElementException()))
                                   .flatMap(b -> universityRepository.deleteById(universityId))
                                   .thenReturn(true);
    }

    public Mono<UniversityResponseDto> updateLogo(Integer universityId, FilePart photo) {
        String filePath = "university/%d/logo/%s".formatted(universityId, photo.filename());
        return universityRepository.findById(universityId)
                                   .switchIfEmpty(Mono.error(NoSuchElementException::new))
                                   .flatMap(student -> azureStorageService.uploadFile(photo, filePath)
                                                                          .flatMap(path -> universityRepository.save(student.logoUrl(path))))
                                   .map(entityToResponseDtoMapper::from);
    }
}
