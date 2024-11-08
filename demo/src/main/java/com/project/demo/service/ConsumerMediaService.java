package com.project.demo.service;

import com.project.demo.entity.ConsumerMedia;
import com.project.demo.repository.ConsumerMediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConsumerMediaService {

    @Autowired
    private ConsumerMediaRepository consumerMediaRepository;

    /**
     * Criar nova relação entre consumer e media
     * @param consumerMedia - relação para ser guardada entre user e media
     * @return Mono com a entidade consumerMedia
     */
    public Mono<ConsumerMedia> createRelationship(ConsumerMedia consumerMedia) {
        return consumerMediaRepository.save(consumerMedia)
                .doOnSuccess(savedRelation -> 
                    log.info("Created relationship between consumer ID: {} and media ID: {}", 
                    savedRelation.getConsumerId(), savedRelation.getMediaId()))
                .doOnError(error -> 
                    log.error("Failed to create relationship: {}", error.getMessage()));
    }

    /**
     * Obter todas as relacoes  
     * @return Flux com todas as relacoes
     */
    public Flux<ConsumerMedia> getAllRelationships() {
        return consumerMediaRepository.findAll()
                .doOnComplete(() -> log.info("Retrieved all consumer-media relationships"))
                .doOnError(error -> log.error("Failed to retrieve relationships: {}", error.getMessage()));
    }

    /**
     * Obter relação específica
     * @param consumerId - ID do consumer
     * @param mediaId - ID da media
     * @return Mono com a relação específica
     */
    public Mono<ConsumerMedia> getRelationship(Long consumerId, Long mediaId) {
        return consumerMediaRepository.findByConsumerIdAndMediaId(consumerId, mediaId)
                .doOnSuccess(relationship -> {
                    if (relationship != null) {
                        log.info("Retrieved relationship between consumer ID: {} and media ID: {}", consumerId, mediaId);
                    }
                })
                .doOnError(error -> 
                    log.error("Failed to retrieve relationship between consumer ID: {} and media ID: {}", consumerId, mediaId, error));
    }

    /**
     * Apagar relação específica
     * @param consumerId - ID do consumer
     * @param mediaId - ID da media
     * @return Mono indicando a conclusão da operação de exclusão
     */
    public Mono<Void> deleteRelationship(Long consumerId, Long mediaId) {
        return consumerMediaRepository.findByConsumerIdAndMediaId(consumerId, mediaId)
                .flatMap(existingRelationship -> consumerMediaRepository.delete(existingRelationship)
                    .doOnSuccess(unused -> 
                        log.info("Deleted relationship between consumer ID: {} and media ID: {}", consumerId, mediaId)))
                .doOnError(error -> 
                    log.error("Failed to delete relationship between consumer ID: {} and media ID: {}", consumerId, mediaId, error));
    }
}
