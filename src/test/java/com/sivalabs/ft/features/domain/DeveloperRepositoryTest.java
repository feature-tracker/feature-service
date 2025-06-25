package com.sivalabs.ft.features.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.ft.features.TestcontainersConfiguration;
import com.sivalabs.ft.features.domain.entities.Developer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
class DeveloperRepositoryTest {

    @Autowired
    private DeveloperRepository developerRepository;

    @Test
    void shouldFindAllDevelopers() {
        List<Developer> developers = developerRepository.findAll();
        assertThat(developers).isNotEmpty();
    }

    @Test
    void shouldFindDeveloperById() {
        // Create a developer first
        Developer newDeveloper = new Developer();
        newDeveloper.setName("Test Developer");
        newDeveloper.setEmailAddress("test@example.com");
        Developer savedDeveloper = developerRepository.save(newDeveloper);
        
        // Then find it by ID
        Optional<Developer> developer = developerRepository.findById(savedDeveloper.getId());
        assertThat(developer).isPresent();
        assertThat(developer.get().getId()).isEqualTo(savedDeveloper.getId());
    }

    @Test
    void shouldSaveDeveloper() {
        Developer developer = new Developer();
        developer.setName("Test Developer");
        developer.setEmailAddress("test@example.com");

        Developer savedDeveloper = developerRepository.save(developer);
        
        assertThat(savedDeveloper.getId()).isNotNull();
        assertThat(savedDeveloper.getName()).isEqualTo("Test Developer");
        assertThat(savedDeveloper.getEmailAddress()).isEqualTo("test@example.com");
    }

    @Test
    void shouldUpdateDeveloper() {
        // Create a developer first
        Developer developer = new Developer();
        developer.setName("Original Name");
        developer.setEmailAddress("original@example.com");
        Developer savedDeveloper = developerRepository.save(developer);
        String originalName = savedDeveloper.getName();
        
        // Update the developer
        savedDeveloper.setName("Updated Name");
        Developer updatedDeveloper = developerRepository.save(savedDeveloper);
        
        assertThat(updatedDeveloper.getId()).isEqualTo(savedDeveloper.getId());
        assertThat(updatedDeveloper.getName()).isEqualTo("Updated Name");
        assertThat(updatedDeveloper.getName()).isNotEqualTo(originalName);
    }

    @Test
    void shouldDeleteDeveloper() {
        // Create a developer to delete
        Developer developer = new Developer();
        developer.setName("Developer to Delete");
        developer.setEmailAddress("delete@example.com");
        Developer savedDeveloper = developerRepository.save(developer);
        
        // Delete the developer
        developerRepository.deleteById(savedDeveloper.getId());
        
        // Verify deletion
        Optional<Developer> deletedDeveloper = developerRepository.findById(savedDeveloper.getId());
        assertThat(deletedDeveloper).isEmpty();
    }
}