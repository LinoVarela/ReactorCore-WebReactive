package com.project.demo.controller;

import com.project.demo.entity.ConsumerMedia;
import com.project.demo.service.ConsumerMediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/relationships")
public class ConsumerMediaController {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerMediaController.class);

    @Autowired
    private ConsumerMediaService consumerMediaService;

    @PostMapping
    public Mono<ResponseEntity<ConsumerMedia>> createRelationship(@RequestBody ConsumerMedia consumerMedia) {
        return consumerMediaService.createRelationship(consumerMedia)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<ConsumerMedia> getAllRelationships() {
        logger.info("Received request to get all consumer-media relationships");
        return consumerMediaService.getAllRelationships();
    }

    @GetMapping("/{consumerId}/{mediaId}")
    public Mono<ResponseEntity<ConsumerMedia>> getRelationship(@PathVariable Long consumerId, @PathVariable Long mediaId) {
        logger.info("Received request for relationship between consumer ID: {} and media ID: {}", consumerId, mediaId);
        return consumerMediaService.getRelationship(consumerId, mediaId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{consumerId}/{mediaId}")
    public Mono<ResponseEntity<Void>> deleteRelationship(@PathVariable Long consumerId, @PathVariable Long mediaId) {
        logger.info("Received request to delete relationship between consumer ID: {} and media ID: {}", consumerId, mediaId);
        return consumerMediaService.deleteRelationship(consumerId, mediaId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(ex -> Mono.just(ResponseEntity.badRequest().build()));
    }
}
