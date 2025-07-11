package com.sivalabs.ft.features.api.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.ft.features.AbstractIT;
import com.sivalabs.ft.features.WithMockOAuth2User;
import com.sivalabs.ft.features.domain.FeatureService;
import com.sivalabs.ft.features.domain.dtos.FeatureDto;
import com.sivalabs.ft.features.domain.models.FeatureStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

class FeatureControllerTests extends AbstractIT {

    @Autowired
    FeatureService featureService;

    @Test
    void shouldGetFeaturesByReleaseCode() {
        var result = mvc.get()
                .uri("/api/features?releaseCode={code}", "IDEA-2023.3.8")
                .exchange();
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.size()")
                .asNumber()
                .isEqualTo(2);
    }

    @Test
    void shouldGetFeatureByCode() {
        String code = "IDEA-1";
        var result = mvc.get().uri("/api/features/{code}", code).exchange();
        assertThat(result).hasStatusOk().bodyJson().convertTo(FeatureDto.class).satisfies(dto -> {
            assertThat(dto.code()).isEqualTo(code);
        });
    }

    @Test
    void shouldReturn404WhenFeatureNotFound() {
        var result = mvc.get().uri("/api/features/{code}", "INVALID_CODE").exchange();
        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldCreateNewFeature() {
        var payload =
                """
                        {
                            "productCode": "intellij",
                            "releaseCode": "IDEA-2023.3.8",
                            "title": "New Feature",
                            "description": "New feature description",
                            "assignedTo": "john.doe"
                        }
                        """;

        var result = mvc.post()
                .uri("/api/features")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .exchange();
        assertThat(result).hasStatus(HttpStatus.CREATED);
        String location = result.getMvcResult().getResponse().getHeader("Location");

        // Verify creation
        assertThat(location).isNotNull();
        var code = location.substring(location.lastIndexOf("/") + 1);

        var getResult = mvc.get().uri(location).exchange();
        assertThat(getResult)
                .hasStatusOk()
                .bodyJson()
                .convertTo(FeatureDto.class)
                .satisfies(dto -> {
                    assertThat(dto.code()).isEqualTo(code);
                    assertThat(dto.title()).isEqualTo("New Feature");
                    assertThat(dto.description()).isEqualTo("New feature description");
                    assertThat(dto.assignedTo()).isEqualTo("john.doe");
                });
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldUpdateFeature() {
        var payload =
                """
            {
                "title": "Updated Feature",
                "description": "Updated description",
                "assignedTo": "jane.doe",
                "status": "IN_PROGRESS"
            }
            """;

        var result = mvc.put()
                .uri("/api/features/{code}", "IDEA-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .exchange();
        assertThat(result).hasStatusOk();

        // Verify the update
        var updatedFeature = mvc.get().uri("/api/features/{code}", "IDEA-1").exchange();
        assertThat(updatedFeature)
                .hasStatusOk()
                .bodyJson()
                .convertTo(FeatureDto.class)
                .satisfies(dto -> {
                    assertThat(dto.title()).isEqualTo("Updated Feature");
                    assertThat(dto.description()).isEqualTo("Updated description");
                    assertThat(dto.assignedTo()).isEqualTo("jane.doe");
                    assertThat(dto.status()).isEqualTo(FeatureStatus.IN_PROGRESS);
                });
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldDeleteFeature() {
        var result = mvc.delete().uri("/api/features/{code}", "IDEA-2").exchange();
        assertThat(result).hasStatusOk();

        // Verify deletion
        var getResult = mvc.get().uri("/api/features/{code}", "IDEA-2").exchange();
        assertThat(getResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldGetFeaturesFromReleaseAndParentReleases() {
        // This test assumes that in the test database:
        // - IDEA-2025.2 is a release with features
        // - IDEA-2025.2.1 is another release with features
        // IDEA-2025.2->IDEA-2025.2.1
        final var featuresCount = createParentReleaseWithFeatures();
        final int childFeaturesCount = createChildReleaseWithFeatures();

        // Now test the new endpoint that should get features from both releases
        // Assuming IDEA-2025.2 is the parent of IDEA-2025.2.1
        var allFeaturesResult = mvc.get()
                .uri("/api/features/all-features?releaseCode={code}", "IDEA-2025.2.1")
                .exchange();

        assertThat(allFeaturesResult).hasStatusOk().bodyJson().extractingPath("$.size()").asNumber()
                .isEqualTo(featuresCount + childFeaturesCount);

        // Test with fromParentRelease parameter
        var limitedFeaturesResult = mvc.get()
                .uri("/api/features/all-features?releaseCode={code}&fromParentRelease={parentCode}",
                        "IDEA-2025.2", "IDEA-2025.2")
                .exchange();

        assertThat(limitedFeaturesResult).hasStatusOk().bodyJson().extractingPath("$.size()").asNumber()
                .isEqualTo(featuresCount);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldGetFeaturesFromReleaseAndParentReleasesWithNativeQuery() {
        final var featuresCount = createParentReleaseWithFeatures();
        final int childFeaturesCount = createChildReleaseWithFeatures();

        assertThat(featureService.findFeaturesByReleaseAndParents("user", "IDEA-2025.2.1", null)).hasSize(featuresCount+childFeaturesCount);
    }

    private int createParentReleaseWithFeatures() {
        var releasePayload = """
                {
                    "productCode": "intellij",
                    "code": "IDEA-2025.2",
                    "description": "IntelliJ IDEA 2025.2"
                }""";

        return createReleaseWithFeatures(releasePayload, "IDEA-2025.2");
    }

    private int createChildReleaseWithFeatures() {
        var releasePayload = """
                {
                    "productCode": "intellij",
                    "code": "IDEA-2025.2.1",
                    "parentCode": "IDEA-2025.2",
                    "description": "IntelliJ IDEA 2025.2.1 Update"
                }""";

        return createReleaseWithFeatures(releasePayload, "IDEA-2025.2.1");
    }

    private int createReleaseWithFeatures(String releasePayload, String releaseCode) {
        var result = mvc.post()
                .uri("/api/releases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(releasePayload)
                .exchange();
        assertThat(result).hasStatus(HttpStatus.CREATED);

        var featurePayload = String.format("""
                {
                    "productCode": "intellij",
                    "releaseCode": "%s",
                    "title": "%s New Feature1",
                    "description": "%s New feature description",
                    "assignedTo": "s.v"
                }
                """, releaseCode, releaseCode, releaseCode);

        final MvcTestResult featureCreationResponse = mvc.post()
                .uri("/api/features")
                .contentType(MediaType.APPLICATION_JSON)
                .content(featurePayload)
                .exchange();
        assertThat(featureCreationResponse).hasStatus(HttpStatus.CREATED);

        var featuresResult = mvc.get()
                .uri("/api/features?releaseCode={code}", releaseCode)
                .exchange();

        assertThat(featuresResult)
                .hasStatusOk().bodyJson().extractingPath("$.size()").asNumber().isEqualTo(1);

        return 1;
    }
}
