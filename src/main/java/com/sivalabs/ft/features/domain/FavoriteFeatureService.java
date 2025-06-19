package com.sivalabs.ft.features.domain;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavoriteFeatureService {
    private final FavoriteFeatureRepository favoriteFeatureRepository;
    private final FeatureRepository featureRepository;

    public FavoriteFeatureService(
            FavoriteFeatureRepository favoriteFeatureRepository, FeatureRepository featureRepository) {
        this.favoriteFeatureRepository = favoriteFeatureRepository;
        this.featureRepository = featureRepository;
    }

    public void addFavoriteFeature(String userId, long featureId) {
        // Check if the feature exists
        featureRepository.findById(featureId).orElseThrow(() -> new EntityNotFoundException("Feature not found"));

        // check if the favorite already exists
        if (favoriteFeatureRepository.existsByUserIdAndFeatureId(userId, featureId)) {
            return;
        }
        FavoriteFeature favoriteFeature = new FavoriteFeature(featureId, userId);
        favoriteFeatureRepository.save(favoriteFeature);
    }

    @Transactional
    public void removeFavoriteFeature(String userId, long featureId) {
        favoriteFeatureRepository.deleteByUserIdAndFeatureId(userId, featureId);
    }
}
