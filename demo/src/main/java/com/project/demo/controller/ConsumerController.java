package com.project.demo.controller;

import com.project.demo.entity.Consumer;
import com.project.demo.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consumers")
public class ConsumerController {

    @Autowired
    private ConsumerService consumerService;

    @PostMapping
    public ResponseEntity<Consumer> createConsumer(@RequestBody Consumer consumer) {
        Consumer createdConsumer = consumerService.createConsumer(consumer);
        return ResponseEntity.ok(createdConsumer);
    }

    @GetMapping
    public ResponseEntity<List<Consumer>> getAllConsumers() {
        List<Consumer> consumerList = consumerService.getAllConsumers();
        return ResponseEntity.ok(consumerList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consumer> getConsumerById(@PathVariable Long id) {
        Consumer consumer = consumerService.getConsumerById(id);
        return consumer != null ? ResponseEntity.ok(consumer) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consumer> updateConsumer(@PathVariable Long id, @RequestBody Consumer consumer) {
        Consumer updatedConsumer = consumerService.updateConsumer(id, consumer);
        return ResponseEntity.ok(updatedConsumer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsumer(@PathVariable Long id) {
        try {
            consumerService.deleteConsumer(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Handle error response
        }
    }
}
