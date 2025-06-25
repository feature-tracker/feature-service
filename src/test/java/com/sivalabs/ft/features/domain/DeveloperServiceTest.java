package com.sivalabs.ft.features.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.ft.features.TestcontainersConfiguration;
import com.sivalabs.ft.features.domain.dtos.DeveloperDto;
import com.sivalabs.ft.features.domain.entities.Developer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Sql(scripts = {"/test-data.sql"})
class DeveloperServiceTest {

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private DeveloperRepository developerRepository;

    @Test
    void shouldGetAllDevelopers() {
        List<DeveloperDto> developers = developerService.getAllDevelopers();
        assertThat(developers).isNotEmpty();
    }

    @Test
    void shouldGetDeveloperById() {
        // Create a developer first
        DeveloperDto developerDto = new DeveloperDto(null, "Test Developer", "test@example.com");
        DeveloperDto createdDeveloper = developerService.createDeveloper(developerDto);
        
        // Then get it by ID
        Optional<DeveloperDto> developer = developerService.getDeveloperById(createdDeveloper.id());
        assertThat(developer).isPresent();
        assertThat(developer.get().id()).isEqualTo(createdDeveloper.id());
        assertThat(developer.get().name()).isEqualTo("Test Developer");
        assertThat(developer.get().emailAddress()).isEqualTo("test@example.com");
    }

    @Test
    void shouldCreateDeveloper() {
        DeveloperDto developerDto = new DeveloperDto(null, "New Developer", "new@example.com");
        
        DeveloperDto createdDeveloper = developerService.createDeveloper(developerDto);
        
        assertThat(createdDeveloper.id()).isNotNull();
        assertThat(createdDeveloper.name()).isEqualTo("New Developer");
        assertThat(createdDeveloper.emailAddress()).isEqualTo("new@example.com");
        
        // Verify in repository
        Optional<Developer> savedDeveloper = developerRepository.findById(createdDeveloper.id());
        assertThat(savedDeveloper).isPresent();
        assertThat(savedDeveloper.get().getName()).isEqualTo("New Developer");
        assertThat(savedDeveloper.get().getEmailAddress()).isEqualTo("new@example.com");
    }

    @Test
    void shouldUpdateDeveloper() {
        // Create a developer first
        DeveloperDto developerDto = new DeveloperDto(null, "Original Name", "original@example.com");
        DeveloperDto createdDeveloper = developerService.createDeveloper(developerDto);
        
        // Update the developer
        DeveloperDto updatedDto = new DeveloperDto(
            createdDeveloper.id(),
            "Updated Name",
            "updated@example.com"
        );
        
        Optional<DeveloperDto> result = developerService.updateDeveloper(createdDeveloper.id(), updatedDto);
        
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(createdDeveloper.id());
        assertThat(result.get().name()).isEqualTo("Updated Name");
        assertThat(result.get().emailAddress()).isEqualTo("updated@example.com");
        
        // Verify in repository
        Optional<Developer> updatedDeveloper = developerRepository.findById(createdDeveloper.id());
        assertThat(updatedDeveloper).isPresent();
        assertThat(updatedDeveloper.get().getName()).isEqualTo("Updated Name");
        assertThat(updatedDeveloper.get().getEmailAddress()).isEqualTo("updated@example.com");
    }

    @Test
    void shouldReturnEmptyWhenUpdatingNonExistentDeveloper() {
        DeveloperDto developerDto = new DeveloperDto(999L, "Non-existent", "nonexistent@example.com");
        
        Optional<DeveloperDto> result = developerService.updateDeveloper(999L, developerDto);
        
        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeleteDeveloper() {
        // Create a developer to delete
        DeveloperDto developerDto = new DeveloperDto(null, "To Delete", "delete@example.com");
        DeveloperDto createdDeveloper = developerService.createDeveloper(developerDto);
        
        // Verify it exists
        assertThat(developerRepository.findById(createdDeveloper.id())).isPresent();
        
        // Delete it
        boolean deleted = developerService.deleteDeveloper(createdDeveloper.id());
        
        // Verify deletion
        assertThat(deleted).isTrue();
        assertThat(developerRepository.findById(createdDeveloper.id())).isEmpty();
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentDeveloper() {
        boolean deleted = developerService.deleteDeveloper(999L);
        assertThat(deleted).isFalse();
    }
}