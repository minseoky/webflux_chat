package com.example.demo.repository;

import com.example.demo.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<UserEntity, String> {

    Mono<UserEntity> findByUsername(String username);

}
