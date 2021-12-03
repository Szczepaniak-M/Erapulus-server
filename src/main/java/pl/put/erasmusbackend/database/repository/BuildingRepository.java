package pl.put.erasmusbackend.database.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.put.erasmusbackend.database.model.BuildingEntity;
import reactor.core.publisher.Flux;

@Repository
public interface BuildingRepository extends R2dbcRepository<BuildingEntity, Integer> {

    @Query("SELECT * FROM building WHERE university = :universityId")
    Flux<BuildingEntity> findByUniversityId(@Param("universityId") int universityId);
}
