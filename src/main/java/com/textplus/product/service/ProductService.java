package com.textplus.product.service;

import com.textplus.product.dto.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto getProduct(String productName);

    List<ProductDto> getAllProducts();

    ProductDto createProduct(ProductDto product);

    ProductDto upsertProduct(ProductDto product);

    ProductDto deleteProduct(String productName);
}
