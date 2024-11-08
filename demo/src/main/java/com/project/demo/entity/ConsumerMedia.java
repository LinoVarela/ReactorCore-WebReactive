package com.project.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("consumer_media")
public class ConsumerMedia {

    @Id
    private Long id;

    private Long consumerId;
    private Long mediaId;

    public ConsumerMedia() {}

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

    @Override
    public String toString() {
        return "ConsumerMedia [id=" + id + ", consumerId=" + consumerId + ", mediaId=" + mediaId + "]";
    }
}
