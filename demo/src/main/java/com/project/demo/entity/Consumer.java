package com.project.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "consumer")
public class Consumer {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Integer age;

    private String gender;

    // Default constructor
    public Consumer() {}

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Consumer [id=" + id + ", name=" + name + ", age=" + age + ", gender=" + gender + "]";
    }

}
