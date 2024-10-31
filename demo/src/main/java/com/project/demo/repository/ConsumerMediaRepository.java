package com.project.demo.repository;

import com.project.demo.entity.ConsumerMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ConsumerMediaRepository extends JpaRepository<ConsumerMedia, Long> {

    Optional<ConsumerMedia> findByConsumerIdAndMediaId(Long consumerId, Long mediaId);

    boolean existsByConsumerId(Long consumerId);

    boolean existsByMediaId(Long mediaId);
}
