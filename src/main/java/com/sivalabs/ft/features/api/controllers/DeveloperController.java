package com.sivalabs.ft.features.api.controllers;

import com.sivalabs.ft.features.domain.DeveloperService;
import com.sivalabs.ft.features.domain.dtos.DeveloperDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/developers")
@Tag(name = "Developers API")
public class DeveloperController {
    private final DeveloperService developerService;

    public DeveloperController(DeveloperService developerService) {
        this.developerService = developerService;
    }

    @GetMapping
    @Operation(
            summary = "Get all developers",
            description = "Get a list of all developers",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful response",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        array = @ArraySchema(schema = @Schema(implementation = DeveloperDto.class))))
            })
    public List<DeveloperDto> getAllDevelopers() {
        return developerService.getAllDevelopers();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get developer by ID",
            description = "Get a developer by their ID",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful response",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = DeveloperDto.class))),
                @ApiResponse(responseCode = "404", description = "Developer not found")
            })
    public ResponseEntity<DeveloperDto> getDeveloperById(@PathVariable Long id) {
        return developerService.getDeveloperById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("")
    @Operation(
            summary = "Create a new developer",
            description = "Create a new developer",
            responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Developer created successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = DeveloperDto.class))),
                @ApiResponse(responseCode = "400", description = "Invalid request")
            })
    public ResponseEntity<DeveloperDto> createDeveloper(@RequestBody @Valid DeveloperDto developerDto) {
        DeveloperDto createdDeveloper = developerService.createDeveloper(developerDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDeveloper.id())
                .toUri();
        return ResponseEntity.created(location).body(createdDeveloper);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing developer",
            description = "Update an existing developer by ID",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Developer updated successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = DeveloperDto.class))),
                @ApiResponse(responseCode = "404", description = "Developer not found"),
                @ApiResponse(responseCode = "400", description = "Invalid request")
            })
    public ResponseEntity<DeveloperDto> updateDeveloper(
            @PathVariable Long id,
            @RequestBody @Valid DeveloperDto developerDto) {
        return developerService.updateDeveloper(id, developerDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a developer",
            description = "Delete a developer by ID",
            responses = {
                @ApiResponse(responseCode = "204", description = "Developer deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Developer not found")
            })
    public ResponseEntity<Void> deleteDeveloper(@PathVariable Long id) {
        boolean deleted = developerService.deleteDeveloper(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}