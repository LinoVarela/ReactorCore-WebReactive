package com.project.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import java.time.LocalDate;

@Table("media") 
public class Media {

    @Id
    private Long id;

    private String title;

    @Column("release_date") 
    private LocalDate releaseDate;

    @Column("average_rating")
    private Short averageRating;

    private String type;

    public Media() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Short getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Short averageRating) {
        this.averageRating = averageRating;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Media [id=" + id + ", title=" + title + ", releaseDate=" + releaseDate + ", averageRating="
                + averageRating + ", type=" + type + "]";
    }
}
