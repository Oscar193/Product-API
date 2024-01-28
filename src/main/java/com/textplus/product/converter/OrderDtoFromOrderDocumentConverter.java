package com.textplus.product.converter;

import com.textplus.product.document.OrderDocument;
import com.textplus.product.dto.OrderDto;
import com.textplus.product.dto.ProductDto;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDtoFromOrderDocumentConverter implements Converter<OrderDocument, OrderDto> {

    private final ProductDtoFromProductDocumentConverter productDtoFromDocConverter;

    @Lazy
    public OrderDtoFromOrderDocumentConverter(ProductDtoFromProductDocumentConverter productDtoFromDocConverter) {
        this.productDtoFromDocConverter = productDtoFromDocConverter;
    }

    @Override
    public OrderDto convert(OrderDocument document) {
        List<ProductDto> productDtos = Collections.emptyList();
        if (ObjectUtils.isNotEmpty(document.getProducts())) {
            productDtos = document.getProducts().stream()
                    .map(productDtoFromDocConverter::convert)
                    .collect(Collectors.toList());
        }

        return new OrderDto(
                UUID.fromString(document.getId()),
                productDtos,
                document.getCreationDateTime(),
                document.getStatus());
    }
}
