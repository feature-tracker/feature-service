package com.sivalabs.ft.features.api.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.ft.features.AbstractIT;
import com.sivalabs.ft.features.WithMockOAuth2User;
import com.sivalabs.ft.features.domain.dtos.DeveloperDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class DeveloperControllerTests extends AbstractIT {

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldGetAllDevelopers() {
        var result = mvc.get()
                .uri("/api/developers")
                .exchange();
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.size()")
                .asNumber()
                .isEqualTo(3);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldGetDeveloperById() {
        // Using developer with ID 100 from the test data
        var result = mvc.get().uri("/api/developers/{id}", 100).exchange();
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(DeveloperDto.class)
                .satisfies(dto -> {
                    assertThat(dto.id()).isEqualTo(100);
                });
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldReturn404WhenDeveloperNotFound() {
        var result = mvc.get().uri("/api/developers/{id}", 999).exchange();
        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldCreateNewDeveloper() {
        var payload =
                """
                {
                    "name": "New Developer",
                    "emailAddress": "new@example.com"
                }
                """;

        var result = mvc.post()
                .uri("/api/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .exchange();
        assertThat(result).hasStatus(HttpStatus.CREATED);
        String location = result.getMvcResult().getResponse().getHeader("Location");

        // Verify creation
        assertThat(location).isNotNull();
        var id = location.substring(location.lastIndexOf("/") + 1);

        var getResult = mvc.get().uri(location).exchange();
        assertThat(getResult)
                .hasStatusOk()
                .bodyJson()
                .convertTo(DeveloperDto.class)
                .satisfies(dto -> {
                    assertThat(dto.id()).isEqualTo(Long.parseLong(id));
                    assertThat(dto.name()).isEqualTo("New Developer");
                    assertThat(dto.emailAddress()).isEqualTo("new@example.com");
                });
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldUpdateDeveloper() {
        var payload =
                """
                {
                    "id": 100,
                    "name": "Updated Developer",
                    "emailAddress": "updated@example.com"
                }
                """;

        var result = mvc.put()
                .uri("/api/developers/{id}", 100)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .exchange();
        assertThat(result).hasStatusOk();

        // Verify the update
        var updatedDeveloper = mvc.get().uri("/api/developers/{id}", 100).exchange();
        assertThat(updatedDeveloper)
                .hasStatusOk()
                .bodyJson()
                .convertTo(DeveloperDto.class)
                .satisfies(dto -> {
                    assertThat(dto.name()).isEqualTo("Updated Developer");
                    assertThat(dto.emailAddress()).isEqualTo("updated@example.com");
                });
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldReturn404WhenUpdatingNonExistentDeveloper() {
        var payload =
                """
                {
                    "id": 999,
                    "name": "Non-existent Developer",
                    "emailAddress": "nonexistent@example.com"
                }
                """;

        var result = mvc.put()
                .uri("/api/developers/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .exchange();
        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldDeleteDeveloper() {
        // First create a developer to delete
        var createPayload =
                """
                {
                    "name": "Developer to Delete",
                    "emailAddress": "delete@example.com"
                }
                """;

        var createResult = mvc.post()
                .uri("/api/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createPayload)
                .exchange();
        String location = createResult.getMvcResult().getResponse().getHeader("Location");
        var id = location.substring(location.lastIndexOf("/") + 1);

        // Now delete it
        var deleteResult = mvc.delete().uri("/api/developers/{id}", id).exchange();
        assertThat(deleteResult).hasStatus(HttpStatus.NO_CONTENT);

        // Verify deletion
        var getResult = mvc.get().uri("/api/developers/{id}", id).exchange();
        assertThat(getResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldReturn404WhenDeletingNonExistentDeveloper() {
        var result = mvc.delete().uri("/api/developers/{id}", 999).exchange();
        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
    }
}