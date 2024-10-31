package com.project.demo.service;

import com.project.demo.entity.ConsumerMedia;
import com.project.demo.repository.ConsumerMediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsumerMediaService {

    @Autowired
    private ConsumerMediaRepository consumerMediaRepository;

    // Method to create a relationship
    public ConsumerMedia createRelationship(ConsumerMedia consumerMedia) {
        return consumerMediaRepository.save(consumerMedia);
    }

    // Method to get all relationships
    public List<ConsumerMedia> getAllRelationships() {
        return consumerMediaRepository.findAll();
    }

    // Method to delete a relationship
    public void deleteRelationship(Long consumerId, Long mediaId) {
        consumerMediaRepository.deleteByConsumerIdAndMediaId(consumerId, mediaId);
    }

    // Optional: Method to check if a relationship exists
    public boolean relationshipExists(Long consumerId, Long mediaId) {
        return consumerMediaRepository.existsByConsumerId(consumerId) && 
               consumerMediaRepository.existsByMediaId(mediaId);
    }
}
