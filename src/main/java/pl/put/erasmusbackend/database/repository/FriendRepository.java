package pl.put.erasmusbackend.database.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pl.put.erasmusbackend.database.model.FriendEntity;

@Repository
public interface FriendRepository extends R2dbcRepository<FriendEntity, Integer> {
}
