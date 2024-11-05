package com.project.demo.controller;

import com.project.demo.entity.Consumer;
import com.project.demo.service.ConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/consumers")
public class ConsumerController {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerController.class);

    @Autowired
    private ConsumerService consumerService;

    @PostMapping
    public Mono<ResponseEntity<Consumer>> createConsumer(@RequestBody Consumer consumer) {
        return consumerService.createConsumer(consumer)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<Consumer> getAllConsumers() {
        logger.info("Received request to get all consumers");
        return consumerService.getAllConsumers();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Consumer>> getConsumerById(@PathVariable Long id) {
        logger.info("Received request for consumer with id: {}", id);
        return consumerService.getConsumerById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Consumer>> updateConsumer(@PathVariable Long id, @RequestBody Consumer consumer) {
        logger.info("Received request to update consumer with id: {}", id);
        return consumerService.updateConsumer(id, consumer)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteConsumer(@PathVariable Long id) {
        logger.info("Received request to delete consumer with id: {}", id);
        return consumerService.deleteConsumer(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(ex -> Mono.just(ResponseEntity.badRequest().build()));
    }
}
