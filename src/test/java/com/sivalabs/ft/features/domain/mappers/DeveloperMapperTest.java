package com.sivalabs.ft.features.domain.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.ft.features.TestcontainersConfiguration;
import com.sivalabs.ft.features.domain.dtos.DeveloperDto;
import com.sivalabs.ft.features.domain.entities.Developer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class DeveloperMapperTest {

    @Autowired
    private DeveloperMapper developerMapper;

    @Test
    void shouldMapDeveloperToDto() {
        // Given
        Developer developer = new Developer();
        developer.setId(1L);
        developer.setName("Test Developer");
        developer.setEmailAddress("test@example.com");

        // When
        DeveloperDto developerDto = developerMapper.toDto(developer);

        // Then
        assertThat(developerDto).isNotNull();
        assertThat(developerDto.id()).isEqualTo(1L);
        assertThat(developerDto.name()).isEqualTo("Test Developer");
        assertThat(developerDto.emailAddress()).isEqualTo("test@example.com");
    }

    @Test
    void shouldMapDtoToDeveloper() {
        // Given
        DeveloperDto developerDto = new DeveloperDto(1L, "Test Developer", "test@example.com");

        // When
        Developer developer = developerMapper.toEntity(developerDto);

        // Then
        assertThat(developer).isNotNull();
        assertThat(developer.getId()).isEqualTo(1L);
        assertThat(developer.getName()).isEqualTo("Test Developer");
        assertThat(developer.getEmailAddress()).isEqualTo("test@example.com");
    }

    @Test
    void shouldHandleNullValues() {
        // Given
        Developer developer = new Developer();
        developer.setId(1L);
        developer.setName("Test Developer");
        developer.setEmailAddress(null);

        // When
        DeveloperDto developerDto = developerMapper.toDto(developer);

        // Then
        assertThat(developerDto).isNotNull();
        assertThat(developerDto.id()).isEqualTo(1L);
        assertThat(developerDto.name()).isEqualTo("Test Developer");
        assertThat(developerDto.emailAddress()).isNull();

        // Given
        DeveloperDto nullEmailDto = new DeveloperDto(2L, "Another Developer", null);

        // When
        Developer mappedDeveloper = developerMapper.toEntity(nullEmailDto);

        // Then
        assertThat(mappedDeveloper).isNotNull();
        assertThat(mappedDeveloper.getId()).isEqualTo(2L);
        assertThat(mappedDeveloper.getName()).isEqualTo("Another Developer");
        assertThat(mappedDeveloper.getEmailAddress()).isNull();
    }
}