package com.sivalabs.ft.features.domain;

import com.sivalabs.ft.features.AbstractIT;
import com.sivalabs.ft.features.domain.entities.Developer;
import com.sivalabs.ft.features.domain.entities.Feature;
import com.sivalabs.ft.features.domain.entities.Product;
import com.sivalabs.ft.features.domain.exceptions.ResourceNotFoundException;
import com.sivalabs.ft.features.domain.models.FeatureStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureEntityTest extends AbstractIT {

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private DeveloperRepository developerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldSaveAndRetrieveFeatureWithDeveloper() {
        // Given
        Developer developer = new Developer();
        developer.setName("Test Developer");
        developer.setEmailAddress("test@example.com");
        Developer savedDeveloper = developerRepository.save(developer);

        Product product = productRepository
                .findByCode("intellij")
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Feature feature = new Feature();
        feature.setTitle("Test Feature");
        feature.setDescription("Test Feature Description");
        feature.setCode("IDEA-10");
        feature.setStatus(FeatureStatus.NEW);
        feature.setCreatedBy("admin");
        feature.setCreatedAt(Instant.now());
        feature.setProduct(product);
        feature.setDeveloper(savedDeveloper);

        // When
        Feature savedFeature = featureRepository.save(feature);

        // Then
        assertThat(savedFeature.getId()).isNotNull();
        assertThat(savedFeature.getTitle()).isEqualTo("Test Feature");
        assertThat(savedFeature.getDeveloper().getId()).isEqualTo(savedDeveloper.getId());

        // Verify retrieval
        Feature retrievedFeature = featureRepository.findById(savedFeature.getId()).orElse(null);
        assertThat(retrievedFeature).isNotNull();
        assertThat(retrievedFeature.getTitle()).isEqualTo("Test Feature");
        assertThat(retrievedFeature.getDeveloper().getId()).isEqualTo(savedDeveloper.getId());
    }
}