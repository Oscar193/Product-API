package com.textplus.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name="Order")
public class OrderDto {

    private UUID id;
    private String buyersEmail;
    private List<ProductDto> products;
    private Instant creationDateTime;
    private OrderStatusEnum status;
    @Setter(AccessLevel.NONE)
    private double totalPrice;

    public OrderDto(UUID id, String buyersEmail, List<ProductDto> products, Instant creationDateTime, OrderStatusEnum status) {
        this.id = id;
        this.buyersEmail = buyersEmail;
        this.products = products;
        this.creationDateTime = creationDateTime;
        this.status = status;
        this.totalPrice = products.stream().mapToDouble(ProductDto::getPrice).sum();
    }
}
