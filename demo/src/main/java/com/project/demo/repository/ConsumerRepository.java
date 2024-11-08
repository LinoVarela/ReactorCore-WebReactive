package com.project.demo.repository;

import com.project.demo.entity.Consumer;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumerRepository extends ReactiveCrudRepository<Consumer, Long> {

}
