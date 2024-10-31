package com.project.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "consumer_media")
public class ConsumerMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consumer_id", nullable = false)
    private Long consumerId;

    @Column(name = "media_id", nullable = false)
    private Long mediaId;

    // Default constructor
    public ConsumerMedia() {}

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(Long consumerId) {
        this.consumerId = consumerId;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    // Optional: toString, equals, and hashCode methods
    @Override
    public String toString() {
        return "ConsumerMedia [id=" + id + ", consumerId=" + consumerId + ", mediaId=" + mediaId + "]";
    }
}
