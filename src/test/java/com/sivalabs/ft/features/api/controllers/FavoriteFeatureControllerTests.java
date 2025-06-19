package com.sivalabs.ft.features.api.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.ft.features.AbstractIT;
import com.sivalabs.ft.features.WithMockOAuth2User;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class FavoriteFeatureControllerTests extends AbstractIT {

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldAddFavoriteFeature() {
        long featureId = 1L;
        var result =
                mvc.post().uri("/api/features/{featureId}/favorites", featureId).exchange();
        assertThat(result).hasStatus(HttpStatus.CREATED);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldReturn400WhenAddingInvalidFeatureId() {
        long invalidFeatureId = 0L;
        var result = mvc.post()
                .uri("/api/features/{featureId}/favorites", invalidFeatureId)
                .exchange();
        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldRemoveFavoriteFeature() {
        long featureId = 1L;
        // First, add to favorites
        mvc.post().uri("/api/features/{featureId}/favorites", featureId).exchange();

        // Then, remove from favorites
        var result = mvc.delete()
                .uri("/api/features/{featureId}/favorites", featureId)
                .exchange();
        assertThat(result).hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldReturn400WhenRemovingInvalidFeatureId() {
        long invalidFeatureId = 0L;
        var result = mvc.delete()
                .uri("/api/features/{featureId}/favorites", invalidFeatureId)
                .exchange();
        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }
}
