package com.textplus.product.controller;

import com.mongodb.client.MongoCollection;
import com.textplus.product.ITBase;
import com.textplus.product.document.OrderDocument;
import com.textplus.product.document.ProductDocument;
import com.textplus.product.dto.OrderDto;
import com.textplus.product.dto.OrderStatusEnum;
import com.textplus.product.dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.mongodb.client.model.Filters.elemMatch;
import static com.mongodb.client.model.Filters.eq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;

@ExtendWith(SpringExtension.class)
public class OrderControllerGetOrderFeatureIT extends ITBase {

    @Override
    protected void populateDatabase() {
        ProductDocument product1 = new ProductDocument("Monster White 500ml", 1.79);
        ProductDocument product2 = new ProductDocument("CocaCola Zero 33ml", 0.78);
        ProductDocument product3 = new ProductDocument("Corconte 1.5L Pack 6 unds", 2.30);
        ProductDocument product4 = new ProductDocument("CocaCola Zero 2L", 1.98);
        ProductDocument product5 = new ProductDocument("CocaCola Zero 2L", 2.0);
        ProductDocument product6 = new ProductDocument("CocaCola 33ml", 0.77);

        getProductColl().insertMany(List.of(product1, product2, product3, product4, product6));

        OrderDocument order1 = new OrderDocument(UUID.nameUUIDFromBytes("order1".getBytes()).toString(), List.of(product1, product2, product3),
                Instant.from(OffsetDateTime.of(2024, 3, 12, 16, 27, 42, 0, ZoneOffset.UTC)),
                OrderStatusEnum.IN_PROGRESS);
        OrderDocument order2 = new OrderDocument(UUID.nameUUIDFromBytes("order2".getBytes()).toString(), List.of(product4),
                Instant.from(OffsetDateTime.of(2024, 2, 12, 16, 27, 42, 0, ZoneOffset.UTC)),
                OrderStatusEnum.DELETED);
        OrderDocument order3 = new OrderDocument(UUID.nameUUIDFromBytes("order3".getBytes()).toString(), List.of(product4, product5),
                Instant.from(OffsetDateTime.of(2024, 4, 12, 16, 27, 42, 0, ZoneOffset.UTC)),
                OrderStatusEnum.FINISHED);
        OrderDocument order4 = new OrderDocument(UUID.nameUUIDFromBytes("order4".getBytes()).toString(), List.of(product2, product3),
                Instant.from(OffsetDateTime.of(2024, 7, 12, 16, 27, 42, 0, ZoneOffset.UTC)),
                OrderStatusEnum.IN_PROGRESS);

        getOrderColl().insertMany(List.of(order1, order2, order3, order4));
    }

    private MongoCollection<ProductDocument> getProductColl() {
        return getCollection("product", ProductDocument.class);
    }

    private MongoCollection<OrderDocument> getOrderColl() {
        return getCollection("order", OrderDocument.class);
    }

    @Test
    public void GetOrder_Should_GetAnOrder() {
        ProductDto product1 = new ProductDto("Monster White 500ml", 1.79);
        ProductDto product2 = new ProductDto("CocaCola Zero 33ml", 0.78);
        ProductDto product3 = new ProductDto("Corconte 1.5L Pack 6 unds", 2.30);
        OrderDto expectedResponse = new OrderDto(UUID.nameUUIDFromBytes("order1".getBytes()), List.of(product1, product2, product3),
                Instant.from(OffsetDateTime.of(2024, 3, 12, 16, 27, 42, 0, ZoneOffset.UTC)),
                OrderStatusEnum.IN_PROGRESS);

        ResponseEntity<OrderDto> response = getTestRestTemplate().exchange(
                "/order/{orderId}",
                GET,
                getEntity(),
                OrderDto.class,
                Map.of("orderId", UUID.nameUUIDFromBytes("order1".getBytes())));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    public void GetOrder_Should_GetNotFound_When_OrderDoesNotExist() {
        ResponseEntity<String> response = getTestRestTemplate().exchange(
                "/order/{orderId}",
                GET,
                getEntity(),
                String.class,
                Map.of("orderId", UUID.nameUUIDFromBytes("NotExistingOrder".getBytes())));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void GetOrders_Should_GetAListOfOrders_When_CreationDatesAreBetweenParams() {
        ProductDto product1 = new ProductDto("Monster White 500ml", 1.79);
        ProductDto product2 = new ProductDto("CocaCola Zero 33ml", 0.78);
        ProductDto product3 = new ProductDto("Corconte 1.5L Pack 6 unds", 2.30);
        ProductDto product4 = new ProductDto("CocaCola Zero 2L", 1.98);
        ProductDto product5 = new ProductDto("CocaCola Zero 2L", 2.0);
        OrderDto order1 = new OrderDto(UUID.nameUUIDFromBytes("order1".getBytes()), List.of(product1, product2, product3),
                Instant.from(OffsetDateTime.of(2024, 3, 12, 16, 27, 42, 0, ZoneOffset.UTC)),
                OrderStatusEnum.IN_PROGRESS);
        OrderDto order2 = new OrderDto(UUID.nameUUIDFromBytes("order2".getBytes()), List.of(product4),
                Instant.from(OffsetDateTime.of(2024, 2, 12, 16, 27, 42, 0, ZoneOffset.UTC)),
                OrderStatusEnum.DELETED);
        OrderDto order3 = new OrderDto(UUID.nameUUIDFromBytes("order3".getBytes()), List.of(product4, product5),
                Instant.from(OffsetDateTime.of(2024, 4, 12, 16, 27, 42, 0, ZoneOffset.UTC)),
                OrderStatusEnum.FINISHED);

        ResponseEntity<List<OrderDto>> response = getTestRestTemplate().exchange(
                "/order/?startCreationDateTime={start}&endCreationDateTime={end}",
                GET,
                getEntity(),
                new ParameterizedTypeReference<>() {},
                Map.of("start", Instant.from(OffsetDateTime.of(2024, 1, 11, 16, 27, 42, 0, ZoneOffset.UTC)),
                        "end", Instant.from(OffsetDateTime.of(2024, 4, 28, 16, 27, 42, 0, ZoneOffset.UTC))));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).containsExactlyInAnyOrder(order1, order2, order3);
    }

    @Test
    public void GetOrders_Should_GetAnEmptyList_When_CreationDatesAreNotBetweenParams() {
        ResponseEntity<List<OrderDto>> response = getTestRestTemplate().exchange(
                "/order/?startCreationDateTime={start}&endCreationDateTime={end}",
                GET,
                getEntity(),
                new ParameterizedTypeReference<>() {},
                Map.of("start", Instant.from(OffsetDateTime.of(2025, 1, 11, 16, 27, 42, 0, ZoneOffset.UTC)),
                        "end", Instant.from(OffsetDateTime.of(2025, 4, 28, 16, 27, 42, 0, ZoneOffset.UTC))));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    public void CreateOrder_Should_CreateAnOrder() {
        List<String> newOrder = List.of("Monster White 500ml", "CocaCola 33ml", "Corconte 1.5L Pack 6 unds");

        ProductDto expectedProductDto1 = new ProductDto("Monster White 500ml", 1.79);
        ProductDto expectedProductDto3 = new ProductDto("Corconte 1.5L Pack 6 unds", 2.30);
        ProductDto expectedProductDto6 = new ProductDto("CocaCola 33ml", 0.77);

        ResponseEntity<OrderDto> response = getTestRestTemplate().exchange(
                "/order/",
                PUT,
                getEntity(newOrder),
                OrderDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getProducts()).isNotEmpty();
        assertThat(response.getBody().getProducts()).containsExactlyInAnyOrder(expectedProductDto1, expectedProductDto3, expectedProductDto6);


        ProductDocument expectedProductDoc1 = new ProductDocument("Monster White 500ml", 1.79);
        ProductDocument expectedProductDoc3 = new ProductDocument("Corconte 1.5L Pack 6 unds", 2.30);
        ProductDocument expectedProductDoc6 = new ProductDocument("CocaCola 33ml", 0.77);

        OrderDocument orderDocument = getOrderColl().find(elemMatch("products",eq("_id", "CocaCola 33ml"))).first();

        assertThat(orderDocument).isNotNull();
        assertThat(orderDocument.getProducts()).isNotEmpty();
        assertThat(orderDocument.getProducts()).containsExactlyInAnyOrder(expectedProductDoc1, expectedProductDoc3, expectedProductDoc6);
    }

    @Test
    public void CreateOrder_Should_CreateAnOrderWithDuplicatedProduct_When_HavingAProductMultipleTimes() {
        List<String> newOrder = List.of("Monster White 500ml", "CocaCola 33ml", "Monster White 500ml");

        ProductDto expectedProductDto1 = new ProductDto("Monster White 500ml", 1.79);
        ProductDto expectedProductDto6 = new ProductDto("CocaCola 33ml", 0.77);

        ResponseEntity<OrderDto> response = getTestRestTemplate().exchange(
                "/order/",
                PUT,
                getEntity(newOrder),
                OrderDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getProducts()).isNotEmpty();
        assertThat(response.getBody().getProducts()).containsExactlyInAnyOrder(expectedProductDto1, expectedProductDto6, expectedProductDto1);


        ProductDocument expectedProductDoc1 = new ProductDocument("Monster White 500ml", 1.79);
        ProductDocument expectedProductDoc6 = new ProductDocument("CocaCola 33ml", 0.77);

        OrderDocument orderDocument = getOrderColl().find(elemMatch("products",eq("_id", "CocaCola 33ml"))).first();

        assertThat(orderDocument).isNotNull();
        assertThat(orderDocument.getProducts()).isNotEmpty();
        assertThat(orderDocument.getProducts()).containsExactlyInAnyOrder(expectedProductDoc1, expectedProductDoc6, expectedProductDoc1);
    }

    @Test
    public void CreateOrder_Should_GetNotFound_When_AtLeastOneProductDoesNotExist() {
        List<String> newOrder = List.of("Monster White 500ml", "MadeUp", "Corconte 1.5L Pack 6 unds");

        ResponseEntity<String> response = getTestRestTemplate().exchange(
                "/order/",
                PUT,
                getEntity(newOrder),
                String.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void AddProducts_Should_AddProductsToOrder() {
        ProductDto product1 = new ProductDto("Monster White 500ml", 1.79);
        ProductDto product2 = new ProductDto("CocaCola Zero 33ml", 0.78);
        ProductDto product3 = new ProductDto("Corconte 1.5L Pack 6 unds", 2.30);
        ProductDto product6 = new ProductDto("CocaCola 33ml", 0.77);
        OrderDto expectedResponse = new OrderDto(UUID.nameUUIDFromBytes("order1".getBytes()), List.of(product1, product2, product3, product6),
                Instant.from(OffsetDateTime.of(2024, 3, 12, 16, 27, 42, 0, ZoneOffset.UTC)),
                OrderStatusEnum.IN_PROGRESS);

        ResponseEntity<OrderDto> response = getTestRestTemplate().exchange(
                "/order/{orderId}?products={products}",
                POST,
                getEntity(),
                OrderDto.class,
                Map.of("orderId", UUID.nameUUIDFromBytes("order1".getBytes()),
                        "products", "CocaCola 33ml"));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(expectedResponse);


        ProductDocument productDoc1 = new ProductDocument("Monster White 500ml", 1.79);
        ProductDocument productDoc2 = new ProductDocument("CocaCola Zero 33ml", 0.78);
        ProductDocument productDoc3 = new ProductDocument("Corconte 1.5L Pack 6 unds", 2.30);
        ProductDocument productDoc6 = new ProductDocument("CocaCola 33ml", 0.77);
        OrderDocument expectedOrderDocument = new OrderDocument(UUID.nameUUIDFromBytes("order1".getBytes()).toString(),
                List.of(productDoc1, productDoc2, productDoc3, productDoc6),
                Instant.from(OffsetDateTime.of(2024, 3, 12, 16, 27, 42, 0, ZoneOffset.UTC)),
                OrderStatusEnum.IN_PROGRESS);

        OrderDocument orderDocument = getOrderColl().find(eq("_id", UUID.nameUUIDFromBytes("order1".getBytes()).toString())).first();

        assertThat(orderDocument).isNotNull();
        assertThat(orderDocument).isEqualTo(expectedOrderDocument);
    }

    @Test
    public void AddProducts_Should_GetNotFound_When_OrderDoesNotExist() {
        ResponseEntity<String> response = getTestRestTemplate().exchange(
                "/order/{orderId}?products={products}",
                POST,
                getEntity(),
                String.class,
                Map.of("orderId", UUID.nameUUIDFromBytes("NotExistingOrder".getBytes()),
                        "products", "CocaCola 33ml"));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void AddProducts_Should_GetNotFound_When_AtLeastOneProductDoesNotExist() {
        ResponseEntity<String> response = getTestRestTemplate().exchange(
                "/order/{orderId}?products={products}",
                POST,
                getEntity(),
                String.class,
                Map.of("orderId", UUID.nameUUIDFromBytes("order1".getBytes()),
                        "products", "NotExistingProduct"));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void DeleteOrder_Should_SetDELETEDStatus() {
        ProductDto product1 = new ProductDto("Monster White 500ml", 1.79);
        ProductDto product2 = new ProductDto("CocaCola Zero 33ml", 0.78);
        ProductDto product3 = new ProductDto("Corconte 1.5L Pack 6 unds", 2.30);
        OrderDto expectedResponse = new OrderDto(UUID.nameUUIDFromBytes("order1".getBytes()), List.of(product1, product2, product3),
                Instant.from(OffsetDateTime.of(2024, 3, 12, 16, 27, 42, 0, ZoneOffset.UTC)),
                OrderStatusEnum.DELETED);

        ResponseEntity<OrderDto> response = getTestRestTemplate().exchange(
                "/order/{orderId}",
                DELETE,
                getEntity(),
                OrderDto.class,
                Map.of("orderId", UUID.nameUUIDFromBytes("order1".getBytes())));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(expectedResponse);


        ProductDocument productDoc1 = new ProductDocument("Monster White 500ml", 1.79);
        ProductDocument productDoc2 = new ProductDocument("CocaCola Zero 33ml", 0.78);
        ProductDocument productDoc3 = new ProductDocument("Corconte 1.5L Pack 6 unds", 2.30);
        OrderDocument expectedOrderDocument = new OrderDocument(UUID.nameUUIDFromBytes("order1".getBytes()).toString(),
                List.of(productDoc1, productDoc2, productDoc3),
                Instant.from(OffsetDateTime.of(2024, 3, 12, 16, 27, 42, 0, ZoneOffset.UTC)),
                OrderStatusEnum.DELETED);

        OrderDocument orderDocument = getOrderColl().find(eq("_id", UUID.nameUUIDFromBytes("order1".getBytes()).toString())).first();

        assertThat(orderDocument).isNotNull();
        assertThat(orderDocument).isEqualTo(expectedOrderDocument);
    }

    @Test
    public void DeleteOrder_Should_GetNotFound_When_OrderDoesNotExist() {
        ResponseEntity<String> response1 = getTestRestTemplate().exchange(
                "/order/{orderId}",
                DELETE,
                getEntity(),
                String.class,
                Map.of("orderId", UUID.nameUUIDFromBytes("order2".getBytes())));

        assertThat(response1).isNotNull();
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);


        ResponseEntity<String> response2 = getTestRestTemplate().exchange(
                "/order/{orderId}",
                DELETE,
                getEntity(),
                String.class,
                Map.of("orderId", UUID.nameUUIDFromBytes("order3".getBytes())));

        assertThat(response2).isNotNull();
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
