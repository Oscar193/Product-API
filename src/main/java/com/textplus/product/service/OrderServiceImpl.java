package com.textplus.product.service;

import com.textplus.product.document.OrderDocument;
import com.textplus.product.document.ProductDocument;
import com.textplus.product.dto.NewOrderDto;
import com.textplus.product.dto.OrderDto;
import com.textplus.product.dto.OrderStatusEnum;
import com.textplus.product.exception.IllegalStatusException;
import com.textplus.product.exception.ElementNotFound;
import com.textplus.product.repository.OrderRepository;
import com.textplus.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ConversionService conversionService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, ConversionService conversionService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.conversionService = conversionService;
    }

    @Override
    public OrderDto getOrder(UUID orderId) {
        return orderRepository.findById(orderId.toString())
                .map(orderDocument -> conversionService.convert(orderDocument, OrderDto.class))
                .orElseThrow(() -> new ElementNotFound("Order not found. Id: " + orderId));
    }

    @Override
    public List<OrderDto> getOrders(Instant startCreationDateTime, Instant endCreationDateTime) {
        return orderRepository.findByCreationDateTimeBetween(startCreationDateTime, endCreationDateTime).stream()
                .map(orderDocument -> conversionService.convert(orderDocument, OrderDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto createOrder(NewOrderDto newOrderDto) {
        Map<String, ProductDocument> exisingProducts = productRepository.findAllById(newOrderDto.getProducts()).stream()
                .collect(Collectors.toMap(ProductDocument::getName, p -> p));

        List<ProductDocument> orderProducts = newOrderDto.getProducts().stream()
                .map(p -> exisingProducts.computeIfAbsent(p, key -> { throw new ElementNotFound("Product not found. Name: " + key);}))
                .toList();

        OrderDocument newOrderDocument = new OrderDocument(UUID.randomUUID().toString(), orderProducts, Instant.now(),
                OrderStatusEnum.IN_PROGRESS);
        OrderDocument createdOrderDocument = orderRepository.insert(newOrderDocument);
        return conversionService.convert(createdOrderDocument, OrderDto.class);
    }

    @Override
    public OrderDto addProducts(UUID orderId, List<String> products) {
        OrderDocument existingOrder = orderRepository.findById(orderId.toString())
                .orElseThrow(() -> new ElementNotFound("Order not found. Id: " + orderId));

        if (!OrderStatusEnum.IN_PROGRESS.equals(existingOrder.getStatus()))
            throw new IllegalStatusException("Order cannot be modified when it is not 'in progress'. Current status: " + existingOrder.getStatus());


        Map<String, ProductDocument> exisingProducts = productRepository.findAllById(products).stream()
                .collect(Collectors.toMap(ProductDocument::getName, p -> p));
        List<ProductDocument> newOrderProducts = products.stream()
                .map(p -> exisingProducts.computeIfAbsent(p, key -> { throw new ElementNotFound("Product not found. Name: " + key);}))
                .toList();

        existingOrder.getProducts().addAll(newOrderProducts);
        OrderDocument newOrderDocument = orderRepository.save(existingOrder);
        return conversionService.convert(newOrderDocument, OrderDto.class);
    }

    @Override
    public OrderDto markOrderAsDeleted(UUID orderId) {
        OrderDocument existingOrder = orderRepository.findById(orderId.toString())
                .orElseThrow(() -> new ElementNotFound("Order ot found. Id: " + orderId));

        if (!OrderStatusEnum.IN_PROGRESS.equals(existingOrder.getStatus()))
            throw new IllegalStatusException("Order cannot be deleted when it is not 'in progress'. Current status: " + existingOrder.getStatus());

        existingOrder.setStatus(OrderStatusEnum.DELETED);
        OrderDocument newOrderDocument = orderRepository.save(existingOrder);
        return conversionService.convert(newOrderDocument, OrderDto.class);
    }
}
