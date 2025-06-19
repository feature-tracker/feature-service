package com.sivalabs.ft.features.api.controllers;

import com.sivalabs.ft.features.api.utils.SecurityUtils;
import com.sivalabs.ft.features.domain.FavoriteFeatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/features/{featureId}/favorites")
@Tag(name = "Favorite Features API")
public class FavoriteFeatureController {

    private final FavoriteFeatureService favoriteFeatureService;

    public FavoriteFeatureController(FavoriteFeatureService favoriteFeatureService) {
        this.favoriteFeatureService = favoriteFeatureService;
    }

    @PostMapping
    @Operation(
            summary = "Add a feature to favorites",
            description = "Add a feature to the user's favorites list",
            responses = {
                @ApiResponse(responseCode = "201", description = "Feature added to favorites successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid feature ID")
            })
    public ResponseEntity<Void> addFavoriteFeature(@PathVariable long featureId) {
        if (featureId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        var username = SecurityUtils.getCurrentUsername();
        favoriteFeatureService.addFavoriteFeature(username, featureId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    @Operation(
            summary = "Remove a feature from favorites",
            description = "Remove a feature from the user's favorites list",
            responses = {
                @ApiResponse(responseCode = "204", description = "Feature removed from favorites successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid feature ID")
            })
    public ResponseEntity<Void> removeFavoriteFeature(@PathVariable long featureId) {
        if (featureId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        var username = SecurityUtils.getCurrentUsername();
        favoriteFeatureService.removeFavoriteFeature(username, featureId);
        return ResponseEntity.noContent().build();
    }
}
