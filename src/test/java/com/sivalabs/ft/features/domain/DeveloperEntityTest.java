package com.sivalabs.ft.features.domain;

import com.sivalabs.ft.features.AbstractIT;
import com.sivalabs.ft.features.domain.entities.Developer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class DeveloperEntityTest extends AbstractIT {

    @Autowired
    private DeveloperRepository developerRepository;

    @Test
    void shouldSaveAndRetrieveDeveloper() {
        // Given
        Developer developer = new Developer();
        developer.setName("Test Developer");
        developer.setEmailAddress("test@example.com");

        // When
        Developer savedDeveloper = developerRepository.save(developer);

        // Then
        assertThat(savedDeveloper.getId()).isNotNull();
        assertThat(savedDeveloper.getName()).isEqualTo("Test Developer");
        assertThat(savedDeveloper.getEmailAddress()).isEqualTo("test@example.com");

        // Verify retrieval
        Developer retrievedDeveloper = developerRepository.findById(savedDeveloper.getId()).orElse(null);
        assertThat(retrievedDeveloper).isNotNull();
        assertThat(retrievedDeveloper.getName()).isEqualTo("Test Developer");
        assertThat(retrievedDeveloper.getEmailAddress()).isEqualTo("test@example.com");
    }
}