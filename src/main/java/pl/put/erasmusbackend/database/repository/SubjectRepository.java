package pl.put.erasmusbackend.database.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pl.put.erasmusbackend.database.model.SubjectEntity;

@Repository
public interface SubjectRepository extends R2dbcRepository<SubjectEntity, Integer> {
}
