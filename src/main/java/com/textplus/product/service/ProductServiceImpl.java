package com.textplus.product.service;

import com.textplus.product.document.ProductDocument;
import com.textplus.product.dto.ProductDto;
import com.textplus.product.exception.ElementAlreadyExist;
import com.textplus.product.exception.ElementNotFound;
import com.textplus.product.repository.ProductRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ConversionService conversionService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ConversionService conversionService) {
        this.productRepository = productRepository;
        this.conversionService = conversionService;
    }

    @Override
    public ProductDto getProduct(String productName) {
        return productRepository.findById(productName)
                .map(productDocument -> conversionService.convert(productDocument, ProductDto.class))
                .orElseThrow(() -> new ElementNotFound("Product not found. Name: " + productName));
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productDocument -> conversionService.convert(productDocument, ProductDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        ProductDocument productDocument = conversionService.convert(productDto, ProductDocument.class);
        ProductDocument newProductDocument;
        try {
            newProductDocument = productRepository.insert(productDocument);
        } catch (DuplicateKeyException mongoExc) {
            log.debug("Product already exists.", mongoExc);
            throw new ElementAlreadyExist("Product already exists. Name: `%s`, Price: ´%s´.", productDocument.getName(), String.valueOf(productDocument.getPrice()));
        }
        return conversionService.convert(newProductDocument, ProductDto.class);
    }

    @Override
    public ProductDto upsertProduct(ProductDto productDto) {
        ProductDocument productDocument = conversionService.convert(productDto, ProductDocument.class);
        ProductDocument newProductDocument = productRepository.save(productDocument);
        return conversionService.convert(newProductDocument, ProductDto.class);
    }

    @Override
    public ProductDto deleteProduct(String productName) {
        ProductDto existingProduct = getProduct(productName);
        productRepository.deleteById(productName);
        return existingProduct;
    }
}
