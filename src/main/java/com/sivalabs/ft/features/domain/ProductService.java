package com.sivalabs.ft.features.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Product> findProductByCode(String code) {
        return productRepository.findByCode(code);
    }

    @Transactional
    public Long createProduct(CreateProductCommand cmd) {
        var product = new Product();
        product.setCode(cmd.code());
        product.setPrefix(cmd.prefix());
        product.setName(cmd.name());
        product.setDescription(cmd.description());
        product.setImageUrl(cmd.imageUrl());
        product.setCreatedBy(cmd.createdBy());
        product.setDisabled(false);
        Product savedProduct = productRepository.save(product);
        return savedProduct.getId();
    }

    @Transactional
    public void updateProduct(UpdateProductCommand cmd) {
        var product = productRepository
                .findByCode(cmd.code())
                .orElseThrow(() -> new ResourceNotFoundException("Product %s not found".formatted(cmd)));
        product.setPrefix(cmd.prefix());
        product.setName(cmd.name());
        product.setDescription(cmd.description());
        product.setImageUrl(cmd.imageUrl());
        product.setUpdatedBy(cmd.updatedBy());
        productRepository.save(product);
    }
}
