package com.textplus.product.controller;

import com.textplus.product.dto.ProductDto;
import com.textplus.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/product")
@Tag(name = "Product", description = "Product operations.")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get a product.")
    @GetMapping(path = {"/{productName}"})
    public ProductDto getProduct(
            @PathVariable("productName")
            @Parameter(description = "Product name", required = true) String productName) {
        return productService.getProduct(productName);
    }

    @Operation(summary = "Get all product.")
    @GetMapping(path = {"/"})
    public List<ProductDto> getAllProduct() {
        return productService.getAllProducts();
    }

    @Operation(summary = "Create a new product.")
    @PutMapping(path = {"/"})
    public ProductDto createProduct(
            @RequestBody ProductDto product) {
        return productService.createProduct(product);
    }

    @Operation(summary = "Upsert a product.")
    @PostMapping(path = {"/"})
    public ProductDto upsertProduct(
            @RequestBody ProductDto product) {
        return productService.upsertProduct(product);
    }

    @Operation(summary = "Delete a product.")
    @DeleteMapping(path = {"/{productName}"})
    public ProductDto deleteProduct(
            @PathVariable("productName")
            @Parameter(description = "Product name", required = true) String productName) {
        return productService.deleteProduct(productName);
    }
}
