package com.project.demo.service;

import com.project.demo.entity.Consumer;
import com.project.demo.repository.ConsumerRepository;
import com.project.demo.repository.ConsumerMediaRepository; // Import for relationship checks
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsumerService {

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private ConsumerMediaRepository consumerMediaRepository; // For relationship checks

    public Consumer createConsumer(Consumer consumer) {
        return consumerRepository.save(consumer);
    }

    public List<Consumer> getAllConsumers() {
        return consumerRepository.findAll();
    }

    public Consumer getConsumerById(Long id) {
        return consumerRepository.findById(id).orElse(null); // Handle not found case as needed
    }

    public Consumer updateConsumer(Long id, Consumer consumer) {
        consumer.setId(id); // Set the ID to update
        return consumerRepository.save(consumer);
    }

    public void deleteConsumer(Long id) {
        // Check if the consumer is connected to any media
        if (consumerMediaRepository.existsByConsumerId(id)) {
            throw new IllegalArgumentException("Cannot delete consumer, it is linked to media.");
        }
        consumerRepository.deleteById(id);
    }
}
