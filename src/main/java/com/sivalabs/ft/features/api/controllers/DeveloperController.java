package com.sivalabs.ft.features.api.controllers;

import com.sivalabs.ft.features.domain.DeveloperService;
import com.sivalabs.ft.features.domain.dtos.DeveloperDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/developers")
public class DeveloperController {
    private final DeveloperService developerService;

    public DeveloperController(DeveloperService developerService) {
        this.developerService = developerService;
    }

    @GetMapping
    public List<DeveloperDto> getAllDevelopers() {
        return developerService.getAllDevelopers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeveloperDto> getDeveloperById(@PathVariable Long id) {
        return developerService.getDeveloperById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<DeveloperDto> createDeveloper(@RequestBody @Valid DeveloperDto developerDto) {
        DeveloperDto createdDeveloper = developerService.createDeveloper(developerDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDeveloper.id())
                .toUri();
        return ResponseEntity.created(location).body(createdDeveloper);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeveloperDto> updateDeveloper(
            @PathVariable Long id,
            @RequestBody @Valid DeveloperDto developerDto) {
        return developerService.updateDeveloper(id, developerDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeveloper(@PathVariable Long id) {
        boolean deleted = developerService.deleteDeveloper(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}