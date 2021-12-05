package com.erapulus.server.database.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import com.erapulus.server.database.model.FriendEntity;

@Repository
public interface FriendRepository extends R2dbcRepository<FriendEntity, Integer> {
}
