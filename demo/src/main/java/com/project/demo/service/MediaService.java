package com.project.demo.service;

import com.project.demo.entity.Media;
import com.project.demo.entity.ConsumerMedia;
import com.project.demo.repository.MediaRepository;
import com.project.demo.repository.ConsumerMediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private ConsumerMediaRepository consumerMediaRepository;

    public Mono<Media> createMedia(Media media) {
        return Mono.fromCallable(() -> mediaRepository.save(media));
    }

    public Flux<Media> getAllMedia() {
        return Flux.fromIterable(mediaRepository.findAll());
    }

    public Mono<Media> getMediaById(Long id) {
        return Mono.justOrEmpty(mediaRepository.findById(id));
    }

    public Mono<Media> updateMedia(Long id, Media media) {
        return Mono.justOrEmpty(mediaRepository.findById(id))
            .flatMap(existingMedia -> {
                media.setId(id);
                return Mono.fromCallable(() -> mediaRepository.save(media));
            });
    }

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
