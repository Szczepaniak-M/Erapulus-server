package pl.put.erasmusbackend.database.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pl.put.erasmusbackend.database.model.BuildingEntity;

@Repository
public interface BuildingRepository extends R2dbcRepository<BuildingEntity, Integer> {
}
