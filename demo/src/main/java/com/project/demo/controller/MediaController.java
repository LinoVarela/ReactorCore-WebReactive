package com.project.demo.controller;

import com.project.demo.entity.Media;
import com.project.demo.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @PostMapping
    public Mono<ResponseEntity<Media>> createMedia(@RequestBody Media media) {
        return mediaService.createMedia(media)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<Media> getAllMedia() {
        return mediaService.getAllMedia();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Media>> getMediaById(@PathVariable Long id) {
        return mediaService.getMediaById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Media>> updateMedia(@PathVariable Long id, @RequestBody Media media) {
        return mediaService.updateMedia(id, media)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMedia(@PathVariable Long id) {
        return mediaService.deleteMedia(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(ex -> Mono.just(ResponseEntity.badRequest().build()));
    }
}
