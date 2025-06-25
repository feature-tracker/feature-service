package com.sivalabs.ft.features.domain;

import com.sivalabs.ft.features.domain.dtos.DeveloperDto;
import com.sivalabs.ft.features.domain.entities.Developer;
import com.sivalabs.ft.features.domain.mappers.DeveloperMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DeveloperService {
    private final DeveloperRepository developerRepository;
    private final DeveloperMapper developerMapper;

    public DeveloperService(DeveloperRepository developerRepository, DeveloperMapper developerMapper) {
        this.developerRepository = developerRepository;
        this.developerMapper = developerMapper;
    }

    @Transactional(readOnly = true)
    public List<DeveloperDto> getAllDevelopers() {
        return developerRepository.findAll().stream()
                .map(developerMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<DeveloperDto> getDeveloperById(Long id) {
        return developerRepository.findById(id)
                .map(developerMapper::toDto);
    }

    @Transactional
    public DeveloperDto createDeveloper(DeveloperDto developerDto) {
        Developer developer = developerMapper.toEntity(developerDto);
        developer.setId(null);
        Developer savedDeveloper = developerRepository.save(developer);
        return developerMapper.toDto(savedDeveloper);
    }

    @Transactional
    public Optional<DeveloperDto> updateDeveloper(Long id, DeveloperDto developerDto) {
        if (!developerRepository.existsById(id)) {
            return Optional.empty();
        }
        Developer developer = developerMapper.toEntity(developerDto);
        developer.setId(id);
        Developer updatedDeveloper = developerRepository.save(developer);
        return Optional.of(developerMapper.toDto(updatedDeveloper));
    }

    @Transactional
    public boolean deleteDeveloper(Long id) {
        if (!developerRepository.existsById(id)) {
            return false;
        }
        developerRepository.deleteById(id);
        return true;
    }
}