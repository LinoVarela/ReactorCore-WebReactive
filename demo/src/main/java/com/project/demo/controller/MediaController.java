package com.project.demo.controller;

import com.project.demo.entity.Media;
import com.project.demo.service.MediaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/media")
@Slf4j
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @PostMapping
    public Mono<ResponseEntity<Media>> createMedia(@RequestBody Media media) {
        log.info("Received request to create media with title: {}", media.getTitle());
        return mediaService.createMedia(media)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<Media> getAllMedia() {
        log.info("Received request to get all media");
        return mediaService.getAllMedia();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Media>> getMediaById(@PathVariable Long id) {
        log.info("Received request for media with id: {}", id);
        return mediaService.getMediaById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Media>> updateMedia(@PathVariable Long id, @RequestBody Media media) {
        log.info("Received request to update media with id: {}", id);
        return mediaService.updateMedia(id, media)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMedia(@PathVariable Long id) {
        log.info("Received request to delete media with id: {}", id);
        return mediaService.deleteMedia(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(ex -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/test") //endpoint de teste para falhas no servidor
    public ResponseEntity<Flux<Media>> getMediaWithRandomFailure() {
        if (Math.random() < 0.99) { /// Simular % de falha
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Simulated network error");
        }    
        Flux<Media> mediaList = mediaService.getAllMedia();
        return ResponseEntity.ok(mediaList);
    }
}
