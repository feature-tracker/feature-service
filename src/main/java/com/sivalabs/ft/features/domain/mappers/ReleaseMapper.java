package com.sivalabs.ft.features.domain.mappers;

import com.sivalabs.ft.features.domain.dtos.ReleaseDto;
import com.sivalabs.ft.features.domain.entities.Release;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ReleaseMapper {
    @Mapping(target = "parentCode", source = "parent", qualifiedByName = "mapParentCode")
    ReleaseDto toDto(Release release);

    @Named("mapParentCode")
    default String mapParentCode(Release parent) {
        return parent != null ? parent.getCode() : null;
    }
}
