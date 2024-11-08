package com.project.demo.service;

import com.project.demo.entity.Media;
import com.project.demo.repository.MediaRepository;
import com.project.demo.repository.ConsumerMediaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private ConsumerMediaRepository consumerMediaRepository;

    /**
     * Criar item media
     * @param media - media (objeto) a ser criada 
     * @return Mono with the created media item
     */
    public Mono<Media> createMedia(Media media) {
        return mediaRepository.save(media)
                .doOnSuccess(savedMedia -> log.info("Created media with ID: {}", savedMedia.getId()))
                .doOnError(error -> log.error("Failed to create media: {}", error.getMessage()));
    }

    /**
     * Obter todas as medias
     * @return Flux with all media items
     */
    public Flux<Media> getAllMedia() {
        return mediaRepository.findAll()
                .doOnComplete(() -> log.info("Retrieved all media items"))
                .doOnError(error -> log.error("Failed to retrieve media items: {}", error.getMessage()));
    }

    /**
     * Obter media especifica
     * @param id - ID da media que se pretende obter
     * @return Mono with the specific media
     */
    public Mono<Media> getMediaById(Long id) {
        return mediaRepository.findById(id)
                .doOnSuccess(media -> {
                    if (media != null) log.info("Retrieved media with ID: {}", id);
                })
                .doOnError(error -> log.error("Failed to retrieve media with ID {}: {}", id, error.getMessage()));
    }

    /**
     * Dar update aos dados de uma media
     * @param id - id da media para dar update
     * @param media - media com os updates
     * @return Mono with the updated media
     */
    public Mono<Media> updateMedia(Long id, Media media) {
        return mediaRepository.findById(id)
                .flatMap(existingMedia -> {
                    media.setId(id); // Ensure we use the correct ID for the update
                    return mediaRepository.save(media);
                })
                .doOnSuccess(updatedMedia -> log.info("Updated media with ID: {}", id))
                .doOnError(error -> log.error("Failed to update media with ID {}: {}", id, error.getMessage()));
    }

    /**
     * Apagar media do repositorio se este não tiver relacao com nenhum consumidor
     * @param id da media para ser apagada
     * @return Mono indicating the completion of delete operation
     */
    public Mono<Void> deleteMedia(Long id) {
        return consumerMediaRepository.existsByMediaId(id)
                .flatMap(hasRelationship -> {
                    if (hasRelationship) {
                        log.warn("Cannot delete media with ID: {} due to existing relationships", id);
                        return Mono.error(new IllegalStateException("Cannot delete media with existing relationships"));
                    } else {
                        return mediaRepository.deleteById(id)
                                .doOnSuccess(unused -> log.info("Deleted media with ID: {}", id));
                    }
                })
                .doOnError(error -> log.error("Failed to delete media with ID {}: {}", id, error.getMessage()));
    }
}
