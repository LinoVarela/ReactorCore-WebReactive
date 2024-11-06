package com.project.demo.service;

import com.project.demo.entity.ConsumerMedia;
import com.project.demo.repository.ConsumerMediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ConsumerMediaService {

    @Autowired
    private ConsumerMediaRepository consumerMediaRepository;

    /**
     * 
     * @param consumerMedia - relacao para ser guardada entre user e media
     * @return entidade consumerMedia
     */
    public Mono<ConsumerMedia> createRelationship(ConsumerMedia consumerMedia) {
        return Mono.just(consumerMediaRepository.save(consumerMedia));
    }

    /**
     * Obter todas as relacoes
     * @return flux com todas as relacoes
     */
    public Flux<ConsumerMedia> getAllRelationships() {
        List<ConsumerMedia> allRelationships = consumerMediaRepository.findAll();
        return Flux.fromIterable(allRelationships);
    }

    /**
     * 
     * @param consumerId - ID do consumer
     * @param mediaId - ID da media
     * @return 
     */
    public Mono<ConsumerMedia> getRelationship(Long consumerId, Long mediaId) {
        return Mono.justOrEmpty(consumerMediaRepository.findByConsumerIdAndMediaId(consumerId, mediaId));
    }

    public Mono<Void> deleteRelationship(Long consumerId, Long mediaId) {
        return Mono.justOrEmpty(consumerMediaRepository.findByConsumerIdAndMediaId(consumerId, mediaId))
            .flatMap(existingRelationship -> {
                consumerMediaRepository.delete(existingRelationship);
                return Mono.empty();
            });
    }
}
