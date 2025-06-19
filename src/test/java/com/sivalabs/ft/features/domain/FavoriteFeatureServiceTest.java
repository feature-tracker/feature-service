package com.sivalabs.ft.features.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sivalabs.ft.features.TestcontainersConfiguration;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Sql(scripts = {"/test-data.sql"})
class FavoriteFeatureServiceTest {

    @Autowired
    private FavoriteFeatureService favoriteFeatureService;

    @Autowired
    private FavoriteFeatureRepository favoriteFeatureRepository;

    @Test
    void testAddFavoriteFeature() {
        long featureId = 1L;
        String userId = "user1";
        favoriteFeatureService.addFavoriteFeature(userId, featureId);

        boolean exists = favoriteFeatureRepository.existsByUserIdAndFeatureId(userId, featureId);
        assertThat(exists).isTrue();
    }

    @Test
    void testAddFavoriteFeatureForNonExistingFeature() {
        String userId = "user2";
        long nonExistingFeatureId = 999L;
        assertThatThrownBy(() -> favoriteFeatureService.addFavoriteFeature(userId, nonExistingFeatureId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void testRemoveFavoriteFeature() {
        long featureId = 2L;
        String userId = "user3";
        favoriteFeatureRepository.save(new FavoriteFeature(featureId, userId));

        favoriteFeatureService.removeFavoriteFeature(userId, featureId);
        boolean exists = favoriteFeatureRepository.existsByUserIdAndFeatureId(userId, featureId);
        assertThat(exists).isFalse();
    }
}
