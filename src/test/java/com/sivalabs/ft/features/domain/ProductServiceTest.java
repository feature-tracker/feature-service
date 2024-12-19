package com.sivalabs.ft.features.domain;

import com.sivalabs.ft.features.TestcontainersConfiguration;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFindProductByCode() {
        Optional<Product> result = productService.findProductByCode("intellij");
        assertTrue(result.isPresent(), "Product with code 'intellij' should be present");
        assertEquals("intellij", result.get().getCode(), "Product code does not match the expected value");
    }

    @Test
    void testFindAllProducts() {
        var products = productService.findAllProducts();
        assertNotNull(products, "The product list should not be null");
        assertFalse(products.isEmpty(), "There must be sample data in the database");
    }

    @Test
    void testCreateProduct() {
        var command = new CreateProductCommand("new-code", "New Product", "Description", "image-url", "user");
        long prev = productRepository.findAll().size();
        Long productId = productService.createProduct(command);
        assertNotNull(productId, "Product ID should not be null after creation");
        var createdProduct = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        assertEquals(command.code(), createdProduct.getCode(), "Product code should match the command");
        assertEquals(prev + 1, productRepository.findAll().size(), "Product repository size should increase by 1 after creation");
    }

    @Test
    void testUpdateProduct() {
        String productCode = "intellij";
        var updateCommand = new UpdateProductCommand(productCode, "Updated Name", "Updated Description", "updated-image-url", "updater");
        productService.updateProduct(updateCommand);
        var updatedProduct = productRepository.findByCode(productCode).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        assertEquals(updateCommand.name(), updatedProduct.getName(), "Product name should match the updated value");
        assertEquals(updateCommand.description(), updatedProduct.getDescription(), "Product description should match the updated value");
        assertEquals(updateCommand.imageUrl(), updatedProduct.getImageUrl(), "Product image URL should match the updated value");
        assertEquals(updateCommand.updatedBy(), updatedProduct.getUpdatedBy(), "Product updatedBy should match the updater's value");
    }

    @Test
    void testUpdateNonExistingProduct() {
        String nonExistentCode = "non-existent";
        var updateCommand = new UpdateProductCommand(nonExistentCode, "Some Name", "Some Description", "some-image-url", "user");
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> productService.updateProduct(updateCommand));
        assertTrue(exception.getMessage().contains(updateCommand.code()), "Error message must contain product code");
    }
}
