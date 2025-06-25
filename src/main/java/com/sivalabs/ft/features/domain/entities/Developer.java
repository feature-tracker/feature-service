package com.sivalabs.ft.features.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "developers")
public class Developer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "developers_id_gen")
    @SequenceGenerator(name = "developers_id_gen", sequenceName = "developer_id_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255) @NotNull @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255) @Column(name = "email_address", unique = true)
    private String emailAddress;

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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}