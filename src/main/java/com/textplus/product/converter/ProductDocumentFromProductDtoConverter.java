package com.textplus.product.converter;

import com.textplus.product.document.ProductDocument;
import com.textplus.product.dto.ProductDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProductDocumentFromProductDtoConverter implements Converter<ProductDto, ProductDocument> {

    @Override
    public ProductDocument convert(ProductDto dto) {
        return ProductDocument.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .build();
    }
}
