package com.sivalabs.ft.features.domain.mappers;

import com.sivalabs.ft.features.domain.dtos.FeatureDto;
import com.sivalabs.ft.features.domain.entities.Feature;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeatureMapper {
    @Mapping(target = "releaseCode", source = "release.code", defaultExpression = "java( null )")
    @Mapping(target = "isFavorite", ignore = true)
    @Mapping(target = "developerId", source = "developer.id", defaultExpression = "java( null )")
    @Mapping(target = "assignedTo", source = "developer.name", defaultExpression = "java( null )")
    FeatureDto toDto(Feature feature);
}
