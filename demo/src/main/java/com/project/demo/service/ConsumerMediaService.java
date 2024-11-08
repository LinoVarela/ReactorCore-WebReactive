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
     * 
     * @param consumerMedia - relacao para ser guardada entre user e media
     * @return entidade consumerMedia
     */
    public Mono<ConsumerMedia> createRelationship(ConsumerMedia consumerMedia) {
        log.info("Creating relationship between consumer ID: {} and media ID: {}", consumerMedia.getConsumerId(), consumerMedia.getMediaId());
        return consumerMediaRepository.save(consumerMedia);
    }


    /**
     * Obter todas as relacoes  
     * @return flux com todas as relacoes
     */
    public Flux<ConsumerMedia> getAllRelationships() {
        log.info("Retrieving all consumer-media relationships");
        return consumerMediaRepository.findAll();
    }

    /**
     * 
     * @param consumerId - ID do consumer
     * @param mediaId - ID da media
     * @return 
     */
    public Mono<ConsumerMedia> getRelationship(Long consumerId, Long mediaId) {
        log.info("Retrieving relationship between consumer ID: {} and media ID: {}", consumerId, mediaId);
        return consumerMediaRepository.findByConsumerIdAndMediaId(consumerId, mediaId);
    }

    public Mono<Void> deleteRelationship(Long consumerId, Long mediaId) {
        log.info("Deleting relationship between consumer ID: {} and media ID: {}", consumerId, mediaId);
        return consumerMediaRepository.findByConsumerIdAndMediaId(consumerId, mediaId)
            .flatMap(existingRelationship -> consumerMediaRepository.delete(existingRelationship));
    }
}
