package com.project.demo.service;

import com.project.demo.entity.Consumer;
import com.project.demo.repository.ConsumerRepository;
import com.project.demo.repository.ConsumerMediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConsumerService {

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private ConsumerMediaRepository consumerMediaRepository;

    /**
     * Criar novo user (User = consumer)
     * @param consumer consumidor para ser criado
     * @return Mono com o novo consumidor
     */
    public Mono<Consumer> createConsumer(Consumer consumer) {
        log.info("Creating new consumer with name: {}", consumer.getName());
        return consumerRepository.save(consumer);
    }

    /**
     * Obter todos os users
     * @return Mono com o consumidor
     */
    public Flux<Consumer> getAllConsumers() {
        log.info("Retrieving all consumers");
        return consumerRepository.findAll();
    }

    /**
     * Obter consumidor por ID
     * @param id do consumidor
     * @return
     */
    public Mono<Consumer> getConsumerById(Long id) {
        log.info("Retrieving consumer with id: {}", id);
        return consumerRepository.findById(id);
    }

    /**
     * Dar update a um consumidor 
     * @param id id do consumidor a ser alterado
     * @param consumer consumidor com os updates
     * @return
     */
    public Mono<Consumer> updateConsumer(Long id, Consumer consumer) {
        log.info("Updatingg consumer with id: {}", id);
        return consumerRepository.findById(id)
            .flatMap(existingConsumer -> {
                consumer.setId(id);
                return consumerRepository.save(consumer);
            });
    }

    /**
     * Apagar consumidor do repositorio se este não tiver relacao com nenhuma media
     * @param id
     * @return
     */
    public Mono<Void> deleteConsumer(Long id) {
        log.info("Attempting to delete consumer with id: {}", id);
        return consumerMediaRepository.existsByConsumerId(id)
            .flatMap(hasRelationship -> {
                if (hasRelationship) {
                    log.warn("Cannot delete consumer with existing relationships");
                    return Mono.error(new IllegalStateException("Cannot delete Consumer with existing relationships"));
                } else {
                    log.info("Consumer with id: {} deleted successfully", id);
                    return consumerRepository.deleteById(id);
                }
            });
    }
}
