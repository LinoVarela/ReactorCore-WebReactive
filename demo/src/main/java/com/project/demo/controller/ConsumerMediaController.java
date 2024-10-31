package com.project.demo.controller;

import com.project.demo.entity.ConsumerMedia;
import com.project.demo.service.ConsumerMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relationships")
public class ConsumerMediaController {

    @Autowired
    private ConsumerMediaService consumerMediaService;

    // Create relationship
    @PostMapping
    public ResponseEntity<ConsumerMedia> createRelationship(@RequestBody ConsumerMedia consumerMedia) {
        ConsumerMedia createdRelationship = consumerMediaService.createRelationship(consumerMedia);
        return ResponseEntity.ok(createdRelationship);
    }

    // Get all relationships
    @GetMapping
    public ResponseEntity<List<ConsumerMedia>> getAllRelationships() {
        List<ConsumerMedia> relationships = consumerMediaService.getAllRelationships();
        return ResponseEntity.ok(relationships);
    }

    // Delete relationship
    @DeleteMapping
    public ResponseEntity<Void> deleteRelationship(@RequestParam Long consumerId, @RequestParam Long mediaId) {
        consumerMediaService.deleteRelationship(consumerId, mediaId);
        return ResponseEntity.noContent().build();
    }
}
