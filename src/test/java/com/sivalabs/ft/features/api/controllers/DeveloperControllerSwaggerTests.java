package com.sivalabs.ft.features.api.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sivalabs.ft.features.AbstractIT;
import com.sivalabs.ft.features.WithMockOAuth2User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.io.IOException;

class DeveloperControllerSwaggerTests extends AbstractIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockOAuth2User(username = "user", roles = { "ADMIN" })
    void shouldHaveCorrectSwaggerDefinitionsForDeveloperController() throws IOException {
        // Get the OpenAPI documentation
        var result = mvc.get()
                .uri("/v3/api-docs")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        // Verify the response is successful
        assertThat(result).hasStatusOk();

        // Parse the JSON response
        String content = result.getMvcResult().getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(content);

        // Verify the Developers API tag exists
        JsonNode tagsNode = rootNode.path("tags");
        assertThat(tagsNode.isArray()).isTrue();
        
        boolean developersTagExists = false;
        for (JsonNode tag : tagsNode) {
            if ("Developers API".equals(tag.path("name").asText())) {
                developersTagExists = true;
                break;
            }
        }
        assertThat(developersTagExists).isTrue();

        // Verify the paths for developer endpoints exist
        JsonNode pathsNode = rootNode.path("paths");
        
        // Check /api/developers endpoint
        JsonNode developersPath = pathsNode.path("/api/developers");
        assertThat(developersPath.isMissingNode()).isFalse();
        
        // Verify GET method
        JsonNode getMethod = developersPath.path("get");
        assertThat(getMethod.isMissingNode()).isFalse();
        assertThat(getMethod.path("summary").asText()).isNotNull();
        
        // Verify POST method
        JsonNode postMethod = developersPath.path("post");
        assertThat(postMethod.isMissingNode()).isFalse();
        assertThat(postMethod.path("summary").asText()).isNotNull();
        
        // Check /api/developers/{id} endpoint
        JsonNode developerByIdPath = pathsNode.path("/api/developers/{id}");
        assertThat(developerByIdPath.isMissingNode()).isFalse();
        
        // Verify GET method for single developer
        JsonNode getByIdMethod = developerByIdPath.path("get");
        assertThat(getByIdMethod.isMissingNode()).isFalse();
        assertThat(getByIdMethod.path("summary").asText()).isNotNull();
        
        // Verify PUT method
        JsonNode putMethod = developerByIdPath.path("put");
        assertThat(putMethod.isMissingNode()).isFalse();
        assertThat(putMethod.path("summary").asText()).isNotNull();
        
        // Verify DELETE method
        JsonNode deleteMethod = developerByIdPath.path("delete");
        assertThat(deleteMethod.isMissingNode()).isFalse();
        assertThat(deleteMethod.path("summary").asText()).isNotNull();
    }
}