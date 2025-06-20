package com.sivalabs.ft.features.domain;

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

    @Transactional
    public void addFavoriteFeature(String userId, String featureCode) {
        // Check if the feature exists
        final Feature feature = featureRepository.findByCode(featureCode)
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found"));

        // check if the favorite already exists
        if (favoriteFeatureRepository.existsByUserIdAndFeatureId(userId, feature.getId())) {
            throw new DuplicateResourceException("Feature is already favorited by the user");
        }
        FavoriteFeature favoriteFeature = new FavoriteFeature(feature.getId(), userId);
        favoriteFeatureRepository.save(favoriteFeature);
    }

    @Transactional
    public void removeFavoriteFeature(String userId, String featureCode) {
        favoriteFeatureRepository.deleteByUserIdAndFeatureCode(userId, featureCode);
    }
}
