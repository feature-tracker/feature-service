package com.sivalabs.ft.features.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.ft.features.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
class FavoriteFeatureRepositoryTest {

    @Autowired
    private FavoriteFeatureRepository favoriteFeatureRepository;

    @Test
    void testExistsByUserIdAndFeatureId() {
        FavoriteFeature favorite = new FavoriteFeature(1L, "user1");
        favoriteFeatureRepository.save(favorite);

        boolean exists = favoriteFeatureRepository.existsByUserIdAndFeatureId("user1", 1L);
        assertThat(exists).isTrue();
    }

    @Test
    void testDeleteByUserIdAndFeatureId() {
        FavoriteFeature favorite = new FavoriteFeature(2L, "user2");
        favoriteFeatureRepository.save(favorite);

        favoriteFeatureRepository.deleteByUserIdAndFeatureId("user2", 2L);
        boolean exists = favoriteFeatureRepository.existsByUserIdAndFeatureId("user2", 2L);
        assertThat(exists).isFalse();
    }
}
