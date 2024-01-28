package com.textplus.product.converter;

import com.textplus.product.document.OrderDocument;
import com.textplus.product.document.ProductDocument;
import com.textplus.product.dto.OrderDto;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDocumentFromOrderDtoConverter implements Converter<OrderDto, OrderDocument> {

    private final ProductDocumentFromProductDtoConverter productDocFromDtoConverter;

    @Lazy
    public OrderDocumentFromOrderDtoConverter(ProductDocumentFromProductDtoConverter productDocFromDtoConverter) {
        this.productDocFromDtoConverter = productDocFromDtoConverter;
    }

    @Override
    public OrderDocument convert(OrderDto dto) {
        List<ProductDocument> productDocuments = null;
        if (ObjectUtils.isNotEmpty(dto.getProducts())) {
            productDocuments = dto.getProducts().stream()
                    .map(productDocFromDtoConverter::convert)
                    .collect(Collectors.toList());
        }

        return OrderDocument.builder()
                .id(dto.getId().toString())
                .buyersEmail(dto.getBuyersEmail())
                .products(productDocuments)
                .creationDateTime(dto.getCreationDateTime())
                .status(dto.getStatus())
                .build();
    }
}
