package com.project.demo.service;

import com.project.demo.entity.Consumer;
import com.project.demo.entity.ConsumerMedia;
import com.project.demo.repository.ConsumerRepository;
import com.project.demo.repository.ConsumerMediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ConsumerService {

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private ConsumerMediaRepository consumerMediaRepository;

    public Mono<Consumer> createConsumer(Consumer consumer) {
        return Mono.fromCallable(() -> consumerRepository.save(consumer));
    }

    public Flux<Consumer> getAllConsumers() {
        return Flux.fromIterable(consumerRepository.findAll());
    }

    public Mono<Consumer> getConsumerById(Long id) {
        return Mono.justOrEmpty(consumerRepository.findById(id));
    }

    public Mono<Consumer> updateConsumer(Long id, Consumer consumer) {
        return Mono.justOrEmpty(consumerRepository.findById(id))
            .flatMap(existingConsumer -> {
                consumer.setId(id);
                return Mono.fromCallable(() -> consumerRepository.save(consumer));
            });
    }

    public Mono<Void> deleteConsumer(Long id) {
        return Mono.justOrEmpty(consumerMediaRepository.existsByConsumerId(id))
            .flatMap(hasRelationship -> {
                if (hasRelationship) {
                    return Mono.error(new IllegalStateException("Cannot delete Consumer with existing relationships"));
                } else {
                    return Mono.fromRunnable(() -> consumerRepository.deleteById(id));
                }
            });
    }
}
