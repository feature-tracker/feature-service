package com.sivalabs.ft.features.api.controllers;

import com.sivalabs.ft.features.api.models.CreateFeaturePayload;
import com.sivalabs.ft.features.api.models.UpdateFeaturePayload;
import com.sivalabs.ft.features.api.utils.SecurityUtils;
import com.sivalabs.ft.features.domain.Commands.CreateFeatureCommand;
import com.sivalabs.ft.features.domain.Commands.DeleteFeatureCommand;
import com.sivalabs.ft.features.domain.Commands.UpdateFeatureCommand;
import com.sivalabs.ft.features.domain.FavoriteFeatureService;
import com.sivalabs.ft.features.domain.FeatureService;
import com.sivalabs.ft.features.domain.dtos.FeatureDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/features")
@Tag(name = "Features API")
class FeatureController {
    private static final Logger log = LoggerFactory.getLogger(FeatureController.class);
    private final FeatureService featureService;
    private final FavoriteFeatureService favoriteFeatureService;

    FeatureController(FeatureService featureService, FavoriteFeatureService favoriteFeatureService) {
        this.featureService = featureService;
        this.favoriteFeatureService = favoriteFeatureService;
    }

    @GetMapping("")
    @Operation(
            summary = "Find features by product or release",
            description = "Find features by product or release with pagination support",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful response",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Page.class)))
            })
    Page<FeatureDto> getFeatures(
            @RequestParam(value = "productCode", required = false) String productCode,
            @RequestParam(value = "releaseCode", required = false) String releaseCode,
            @PageableDefault Pageable pageable) {
        // Only one of productCode or releaseCode should be provided
        if ((StringUtils.isBlank(productCode) && StringUtils.isBlank(releaseCode))
                || (StringUtils.isNotBlank(productCode) && StringUtils.isNotBlank(releaseCode))) {
            // TODO: Return 400 Bad Request
            return Page.empty();
        }
        String username = SecurityUtils.getCurrentUsername();
        if (StringUtils.isNotBlank(productCode)) {
            return featureService.findFeaturesByProductPageable(username, productCode, pageable);
        } else {
            return featureService.findFeaturesByReleasePageable(username, releaseCode, pageable);
        }
    }

    @GetMapping("/{code}")
    @Operation(
            summary = "Find feature by code",
            description = "Find feature by code",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful response",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = FeatureDto.class))),
                @ApiResponse(responseCode = "404", description = "Feature not found")
            })
    ResponseEntity<FeatureDto> getFeature(@PathVariable String code) {
        String username = SecurityUtils.getCurrentUsername();
        Optional<FeatureDto> featureDtoOptional = featureService.findFeatureByCode(username, code);
        if (username != null && featureDtoOptional.isPresent()) {
            FeatureDto featureDto = featureDtoOptional.get();
            Set<String> featureCodes = Set.of(featureDto.code());
            Map<String, Boolean> favoriteFeatures = favoriteFeatureService.getFavoriteFeatures(username, featureCodes);
            featureDto = featureDto.makeFavorite(favoriteFeatures.get(featureDto.code()));
            featureDtoOptional = Optional.of(featureDto);
        }
        return featureDtoOptional
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("")
    @Operation(
            summary = "Create a new feature",
            description = "Create a new feature",
            responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Successful response",
                        headers =
                                @Header(
                                        name = "Location",
                                        required = true,
                                        description = "URI of the created feature")),
                @ApiResponse(responseCode = "400", description = "Invalid request"),
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "403", description = "Forbidden"),
            })
    ResponseEntity<Void> createFeature(@RequestBody @Valid CreateFeaturePayload payload) {
        var username = SecurityUtils.getCurrentUsername();
        var cmd = new CreateFeatureCommand(
                payload.productCode(),
                payload.releaseCode(),
                payload.title(),
                payload.description(),
                payload.developerId(),
                username);
        String code = featureService.createFeature(cmd);
        log.info("Created feature with code {}", code);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{code}")
                .buildAndExpand(code)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{code}")
    @Operation(
            summary = "Update an existing feature",
            description = "Update an existing feature",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful response"),
                @ApiResponse(responseCode = "400", description = "Invalid request"),
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "403", description = "Forbidden"),
            })
    void updateFeature(@PathVariable String code, @RequestBody UpdateFeaturePayload payload) {
        var username = SecurityUtils.getCurrentUsername();
        var cmd = new UpdateFeatureCommand(
                code,
                payload.title(),
                payload.description(),
                payload.status(),
                payload.releaseCode(),
                payload.developerId(),
                username);
        featureService.updateFeature(cmd);
    }

    @DeleteMapping("/{code}")
    @Operation(
            summary = "Delete an existing feature",
            description = "Delete an existing feature",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful response"),
                @ApiResponse(responseCode = "400", description = "Invalid request"),
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "403", description = "Forbidden"),
            })
    ResponseEntity<Void> deleteFeature(@PathVariable String code) {
        var username = SecurityUtils.getCurrentUsername();
        if (!featureService.isFeatureExists(code)) {
            return ResponseEntity.notFound().build();
        }
        var cmd = new DeleteFeatureCommand(code, username);
        featureService.deleteFeature(cmd);
        return ResponseEntity.ok().build();
    }
}
