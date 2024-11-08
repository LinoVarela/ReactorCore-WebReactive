package com.project.demo.repository;

import com.project.demo.entity.ConsumerMedia;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ConsumerMediaRepository extends ReactiveCrudRepository<ConsumerMedia, Long> {

    Mono<ConsumerMedia> findByConsumerIdAndMediaId(Long consumerId, Long mediaId);

    Mono<Boolean> existsByConsumerId(Long consumerId);

    Mono<Boolean> existsByMediaId(Long mediaId);
}
