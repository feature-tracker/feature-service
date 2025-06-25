package com.sivalabs.ft.features.domain;

import com.sivalabs.ft.features.domain.entities.Feature;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

interface FeatureRepository extends ListCrudRepository<Feature, Long>, PagingAndSortingRepository<Feature, Long> {
    @Query("select f from Feature f left join fetch f.release where f.code = :code")
    Optional<Feature> findByCode(String code);

    @Query("select f from Feature f left join fetch f.release where f.release.code = :releaseCode")
    List<Feature> findByReleaseCode(String releaseCode);
    
    @Query("select f from Feature f where f.release.code = :releaseCode")
    Page<Feature> findByReleaseCode(String releaseCode, Pageable pageable);

    @Query("select f from Feature f left join fetch f.release where f.product.code = :productCode")
    List<Feature> findByProductCode(String productCode);
    
    @Query("select f from Feature f where f.product.code = :productCode")
    Page<Feature> findByProductCode(String productCode, Pageable pageable);

    @Modifying
    void deleteByCode(String code);

    @Modifying
    @Query("delete from Feature f where f.release.code = :code")
    void deleteByReleaseCode(String code);

    boolean existsByCode(String code);

    @Query(value = "select nextval('feature_code_seq')", nativeQuery = true)
    long getNextFeatureId();
}
