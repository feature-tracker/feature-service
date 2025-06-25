package com.sivalabs.ft.features.domain;

import com.sivalabs.ft.features.domain.entities.Developer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.ListCrudRepository;

public interface DeveloperRepository extends ListCrudRepository<Developer, Long> {
}