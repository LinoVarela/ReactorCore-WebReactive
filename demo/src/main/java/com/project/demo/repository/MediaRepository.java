package com.project.demo.repository;

import com.project.demo.entity.Media;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends ReactiveCrudRepository<Media, Long> {

}
