package com.project.demo.service;

import com.project.demo.entity.Media;
import com.project.demo.repository.MediaRepository;
import com.project.demo.repository.ConsumerMediaRepository;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;

@Service
public class MediaService {

    private static final Logger logger = LoggerFactory.getLogger(Media.class);

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private ConsumerMediaRepository consumerMediaRepository;

    /**
     * Criar item media
     * @param media - media (objeto) a ser criada 
     * @return
     */
    public Mono<Media> createMedia(Media media) {
        return Mono.fromCallable(() -> mediaRepository.save(media));
    }

    /**
     * Obter todas as medias
     * @return
     */
    public Flux<Media> getAllMedia() {
        return Flux.fromIterable(mediaRepository.findAll())
        .doOnNext(media -> logger.info("Retrieved media: {}", media))
        .onErrorResume(error -> {
            logger.error("Error retrieving media", error);
            return Flux.empty();
        });
    }

    /**
     * Obter media especifica
     * @param id - ID da media que se pretende obter
     * @return
     */
    public Mono<Media> getMediaById(Long id) {
        return Mono.justOrEmpty(mediaRepository.findById(id));
    }


    /**
     * Dar update aos dados de uma media
     * @param id - id da media para dar update
     * @param media - media com os updates
     * @return
     */
    public Mono<Media> updateMedia(Long id, Media media) {
        return Mono.justOrEmpty(mediaRepository.findById(id))
            .flatMap(existingMedia -> {
                media.setId(id);
                return Mono.fromCallable(() -> mediaRepository.save(media));
            });
    }

    /**
     * Apagar media do repositorio se este não tiver relacao com nenhum consumidor
     * @param id da media para ser apagada
     * @return
     */
    public Mono<Void> deleteMedia(Long id) {
        return Mono.justOrEmpty(consumerMediaRepository.existsByMediaId(id))
            .flatMap(hasRelationship -> {
                if (hasRelationship) {
                    return Mono.error(new IllegalStateException("Cannot delete Media with existing relationships"));
                } else {
                    return Mono.fromRunnable(() -> mediaRepository.deleteById(id));
                }
            });
    }
}
