package com.sivalabs.ft.features.domain.dtos;

import java.io.Serializable;

public record DeveloperDto(
        Long id,
        String name,
        String emailAddress) implements Serializable {
}