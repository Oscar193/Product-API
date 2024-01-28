package com.textplus.product.service;

import com.textplus.product.dto.OrderDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDto getOrder(UUID orderId);

    List<OrderDto> getOrders(Instant startCreationDateTime, Instant endCreationDateTime);

    OrderDto createOrder(List<String> products);

    OrderDto addProducts(UUID orderId, List<String> products);

    OrderDto markOrderAsDeleted(UUID orderId);
}
