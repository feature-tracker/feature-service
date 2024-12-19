package com.sivalabs.ft.features.api;

import com.sivalabs.ft.features.TestcontainersConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
public class ProductApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }

    @Test
    void testCreateProduct() throws Exception {
        String productJson = """
                    {
                        "code": "new-product",
                        "name": "New Product",
                        "description": "A newly created product",
                        "imageUrl": "http://example.com/image.png"
                    }
                """.stripIndent();

        mockMvc.perform(post("/api/products")
                        .with(jwt().jwt(builder -> builder
                                        .claim("preferred_username", "testuser")
                                        .claim("realm_access", Map.of("roles", List.of("USER")))
                                )
                        )
                        .contentType("application/json")
                        .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "http://localhost/api/products/new-product"));
    }

    @Test
    void testCreateProductUnauthorized() throws Exception {
        String productJson = """
                    {
                        "code": "new-product",
                        "name": "New Product",
                        "description": "A newly created product",
                        "imageUrl": "http://example.com/image.png"
                    }
                """.stripIndent();

        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content(productJson))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void testUpdateProduct() throws Exception {
        String updatedProductJson = """
                    {
                        "name": "Updated Product",
                        "description": "This is an updated product description",
                        "imageUrl": "http://example.com/updated-image.png"
                    }
                """.stripIndent();

        String productCode = "intellij";

        mockMvc.perform(put("/api/products/{code}", productCode)
                        .with(jwt().jwt(builder -> builder
                                        .claim("preferred_username", "testuser")
                                        .claim("realm_access", Map.of("roles", List.of("USER")))
                                )
                        )
                        .contentType("application/json")
                        .content(updatedProductJson))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateProductUnauthorized() throws Exception {
        String updatedProductJson = """
                    {
                        "name": "Updated Product",
                        "description": "This is an updated product description",
                        "imageUrl": "http://example.com/updated-image.png"
                    }
                """.stripIndent();

        String productCode = "intellij";

        mockMvc.perform(put("/api/products/{code}", productCode)
                        .contentType("application/json")
                        .content(updatedProductJson))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void testGetNonExistingProduct() throws Exception {
        String nonExistingProductCode = "nonexistent-code";
        mockMvc.perform(get("/api/products/{code}", nonExistingProductCode))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetProduct() throws Exception {
        String productCode = "intellij";
        mockMvc.perform(get("/api/products/{code}", productCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(productCode))
                .andExpect(jsonPath("$.name").isNotEmpty());
    }

}
