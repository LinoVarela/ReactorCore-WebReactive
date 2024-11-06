package com.project.demo.service;

import com.project.demo.entity.Consumer;
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

    /**
     * Criar novo user (User = consumer)
     * @param consumer consumidor para ser criado
     * @return Mono com o novo consumidor
     */
    public Mono<Consumer> createConsumer(Consumer consumer) {
        return Mono.fromCallable(() -> consumerRepository.save(consumer));
    }

    /**
     * Obter todos os users
     * @return Mono com o consumidor
     */
    public Flux<Consumer> getAllConsumers() {
        return Flux.fromIterable(consumerRepository.findAll());
    }

    /**
     * Obter consumidor por ID
     * @param id do consumidor
     * @return
     */
    public Mono<Consumer> getConsumerById(Long id) {
        return Mono.justOrEmpty(consumerRepository.findById(id));
    }

    /**
     * Dar update a um consumidor 
     * @param id id do consumidor a ser alterado
     * @param consumer consumidor com os updates
     * @return
     */
    public Mono<Consumer> updateConsumer(Long id, Consumer consumer) {
        return Mono.justOrEmpty(consumerRepository.findById(id))
            .flatMap(existingConsumer -> {
                consumer.setId(id);
                return Mono.fromCallable(() -> consumerRepository.save(consumer));
            });
    }

    /**
     * Apagar consumidor do repositorio se este não tiver relacao com nenhuma media
     * @param id
     * @return
     */
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
