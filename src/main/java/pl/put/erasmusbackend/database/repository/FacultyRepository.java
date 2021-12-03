package pl.put.erasmusbackend.database.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.put.erasmusbackend.database.model.FacultyEntity;
import reactor.core.publisher.Mono;

@Repository
public interface FacultyRepository extends R2dbcRepository<FacultyEntity, Integer> {

    @Query("SELECT * FROM faculty WHERE id = :facultyId AND university = :universityId")
    Mono<FacultyEntity> findByIdAndUniversity(@Param("facultyId") int facultyId,
                                              @Param("universityId") int universityId);
}
