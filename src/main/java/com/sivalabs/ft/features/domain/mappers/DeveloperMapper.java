package com.sivalabs.ft.features.domain.mappers;

import com.sivalabs.ft.features.domain.dtos.DeveloperDto;
import com.sivalabs.ft.features.domain.entities.Developer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeveloperMapper {
    DeveloperDto toDto(Developer developer);
    Developer toEntity(DeveloperDto developerDto);
}