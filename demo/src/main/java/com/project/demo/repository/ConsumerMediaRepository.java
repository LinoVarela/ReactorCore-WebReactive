package com.project.demo.repository;

import com.project.demo.entity.ConsumerMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ConsumerMediaRepository extends JpaRepository<ConsumerMedia, Long> {

    // Method to check if a relationship exists by consumer ID
    boolean existsByConsumerId(Long consumerId);

    // Method to check if a relationship exists by media ID
    boolean existsByMediaId(Long mediaId);

    // Optional: Method to delete by composite key if needed
    void deleteByConsumerIdAndMediaId(Long consumerId, Long mediaId);
}
