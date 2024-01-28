package com.textplus.product.controller;

import com.textplus.product.dto.OrderDto;
import com.textplus.product.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/order")
@Tag(name = "Order", description = "Order operations.")
@Validated
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Get an order.")
    @GetMapping(path = {"/{orderId}"})
    public OrderDto getOrder(
            @PathVariable("orderId")
            @Parameter(description = "Order Id", required = true) UUID orderId) {
        return orderService.getOrder(orderId);
    }

    @Operation(summary = "Get a list of orders based on creation dateTime.")
    @GetMapping(path = {"/"})
    public List<OrderDto> getOrders(
            @RequestParam("startCreationDateTime")
            @Parameter(description = "Start creation datetime", required = true) Instant startCreationDateTime,
            @RequestParam("endCreationDateTime")
            @Parameter(description = "End creation datetime", required = true) Instant endCreationDateTime) {
        return orderService.getOrders(startCreationDateTime, endCreationDateTime);
    }

    @Operation(summary = "Create an order.")
    @PutMapping(path = {"/"})
    public OrderDto createOrder(
            @RequestBody @NotEmpty List<String> products) {
        return orderService.createOrder(products);
    }

    @Operation(summary = "Add products to an order.")
    @PostMapping(path = {"/{orderId}"})
    public OrderDto addProducts(
            @PathVariable("orderId")
            @Parameter(description = "Order Id", required = true) UUID orderId,
            @RequestParam("products")
            @Parameter(description = "Products to add to the order", required = true) @NotEmpty List<String> products) {
        return orderService.addProducts(orderId, products);
    }

    @Operation(summary = "Marks an order as deleted.")
    @DeleteMapping(path = {"/{orderId}"})
    public OrderDto deleteOrder(
            @PathVariable("orderId")
            @Parameter(description = "Order Id", required = true) UUID orderId) {
        return orderService.markOrderAsDeleted(orderId);
    }
}
