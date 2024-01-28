package com.textplus.product.converter;

import com.textplus.product.document.ProductDocument;
import com.textplus.product.dto.ProductDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoFromProductDocumentConverter implements Converter<ProductDocument, ProductDto> {

    @Override
    public ProductDto convert(ProductDocument document) {
        return ProductDto.builder()
                .name(document.getName())
                .price(document.getPrice())
                .build();
    }
}
