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
        return consumerRepository.save(consumer)
                .doOnSuccess(savedConsumer -> log.info("Created new consumer with ID: {}", savedConsumer.getId()))
                .doOnError(error -> log.error("Failed to create consumer: {}", error.getMessage()));
    }

    /**
     * Obter todos os users
     * @return Mono com o consumidor
     */
    public Flux<Consumer> getAllConsumers() {
        return consumerRepository.findAll()
                .doOnComplete(() -> log.info("Retrieved all consumers"))
                .doOnError(error -> log.error("Failed to retrieve consumers: {}", error.getMessage()));
    }

    /**
     * Obter consumidor por ID
     * @param id do consumidor
     * @return Mono com o consumidor específico
     */
    public Mono<Consumer> getConsumerById(Long id) {
        return consumerRepository.findById(id)
                .doOnSuccess(consumer -> {
                    if (consumer != null) log.info("Retrieved consumer with ID: {}", id);
                })
                .doOnError(error -> log.error("Failed to retrieve consumer with ID {}: {}", id, error.getMessage()));
    }

    /**
     * Dar update a um consumidor 
     * @param id id do consumidor a ser alterado
     * @param consumer consumidor com os updates
     * @return Mono com o consumidor atualizado
     */
    public Mono<Consumer> updateConsumer(Long id, Consumer consumer) {
        return consumerRepository.findById(id)
                .flatMap(existingConsumer -> {
                    consumer.setId(id);
                    return consumerRepository.save(consumer);
                })
                .doOnSuccess(updatedConsumer -> log.info("Updated consumer with ID: {}", id))
                .doOnError(error -> log.error("Failed to update consumer with ID {}: {}", id, error.getMessage()));
    }

    /**
     * Apagar consumidor do repositorio se este não tiver relacao com nenhuma media
     * @param id ID do consumidor a ser apagado
     * @return Mono indicando a conclusão da operação de exclusão
     */
    public Mono<Void> deleteConsumer(Long id) {
        return consumerMediaRepository.existsByConsumerId(id)
                .flatMap(hasRelationship -> {
                    if (hasRelationship) {
                        log.warn("Cannot delete consumer with ID: {} due to existing relationships", id);
                        return Mono.error(new IllegalStateException("Cannot delete Consumer with existing relationships"));
                    } else {
                        return consumerRepository.deleteById(id)
                                .doOnSuccess(unused -> log.info("Deleted consumer with ID: {}", id));
                    }
                })
                .doOnError(error -> log.error("Failed to delete consumer with ID {}: {}", id, error.getMessage()));
    }
}
