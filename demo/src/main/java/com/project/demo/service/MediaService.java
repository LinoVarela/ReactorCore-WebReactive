package com.project.demo.service;

import com.project.demo.entity.Media;
import com.project.demo.repository.MediaRepository;
import com.project.demo.repository.ConsumerMediaRepository; // Import for relationship checks
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private ConsumerMediaRepository consumerMediaRepository; // For relationship checks

    public Media createMedia(Media media) {
        return mediaRepository.save(media);
    }

    public List<Media> getAllMedia() {
        return mediaRepository.findAll();
    }

    public Media getMediaById(Long id) {
        return mediaRepository.findById(id).orElse(null); // Handle not found case as needed
    }

    public Media updateMedia(Long id, Media media) {
        media.setId(id); // Set the ID to update
        return mediaRepository.save(media);
    }

    public void deleteMedia(Long id) {
        // Check if the media is connected to any consumer
        if (consumerMediaRepository.existsByMediaId(id)) {
            throw new IllegalArgumentException("Cannot delete media, it is linked to a consumer.");
        }
        mediaRepository.deleteById(id);
    }
}
