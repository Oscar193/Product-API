package com.textplus.product.controller;

import com.mongodb.client.MongoCollection;
import com.textplus.product.ITBase;
import com.textplus.product.document.ProductDocument;
import com.textplus.product.dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;

@ExtendWith(SpringExtension.class)
public class ProductControllerGetProductFeatureIT extends ITBase {

    @Override
    protected void populateDatabase() {
        ProductDocument product1 = new ProductDocument("Monster White 500ml", 1.79);
        ProductDocument product2 = new ProductDocument("CocaCola Zero 33ml", 0.78);
        ProductDocument product3 = new ProductDocument("Corconte 1.5L Pack 6 unds", 2.30);
        ProductDocument product4 = new ProductDocument("CocaCola Zero 2L", 1.98);

        getProductColl().insertMany(List.of(product1, product2, product3, product4));
    }

    private MongoCollection<ProductDocument> getProductColl() {
        return getCollection("product", ProductDocument.class);
    }

    @Test
    public void GetProduct_Should_GetAProduct_When_ProductExists() {
        ProductDto expectedResponse = new ProductDto("CocaCola Zero 33ml", 0.78);

        ResponseEntity<ProductDto> response = getTestRestTemplate().exchange(
                "/product/{productName}",
                GET,
                getEntity(),
                ProductDto.class,
                Map.of("productName", "CocaCola Zero 33ml"));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    public void GetProduct_Should_GetNotFound_When_ProductDoesNotExist() {
        ResponseEntity<String> response = getTestRestTemplate().exchange(
                "/product/{productName}",
                GET,
                getEntity(),
                String.class,
                Map.of("productName", "NotExists"));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void GetAllProducts_Should_GetAllProducts() {
        ProductDto expectedProduct1 = new ProductDto("Monster White 500ml", 1.79);
        ProductDto expectedProduct2 = new ProductDto("CocaCola Zero 33ml", 0.78);
        ProductDto expectedProduct3 = new ProductDto("Corconte 1.5L Pack 6 unds", 2.30);
        ProductDto expectedProduct4 = new ProductDto("CocaCola Zero 2L", 1.98);

        ResponseEntity<List<ProductDto>> response = getTestRestTemplate().exchange(
                "/product/",
                GET,
                getEntity(),
                new ParameterizedTypeReference<>() {});

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).containsExactlyInAnyOrder(expectedProduct1, expectedProduct2, expectedProduct3,
                expectedProduct4);
    }

    @Test
    public void CreateProduct_Should_CreateAProduct() {
        ProductDto newProduct = new ProductDto("CocaCola Light 33ml", 0.78);

        ResponseEntity<ProductDto> response = getTestRestTemplate().exchange(
                "/product/",
                PUT,
                getEntity(newProduct),
                ProductDto.class);

        ProductDocument productDocument = getProductColl().find(eq("_id", "CocaCola Light 33ml")).first();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(productDocument).isNotNull();
        assertThat(response.getBody()).isEqualTo(newProduct);
        assertThat(response.getBody().getName()).isEqualTo(productDocument.getName());
        assertThat(response.getBody().getPrice()).isEqualTo(productDocument.getPrice());
    }

    @Test
    public void CreateProduct_Should_GetConflict_When_ProductAlreadyExists() {
        ProductDto expectedResponse = new ProductDto("CocaCola Zero 33ml", 0.78);

        ResponseEntity<String> response = getTestRestTemplate().exchange(
                "/product/",
                PUT,
                getEntity(expectedResponse),
                String.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void UpsertProduct_Should_CreateProduct_When_ProductDoesNotExist() {
        ProductDto newProduct = new ProductDto("CocaCola Light 33ml", 0.78);

        ResponseEntity<ProductDto> response = getTestRestTemplate().exchange(
                "/product/",
                POST,
                getEntity(newProduct),
                ProductDto.class);

        ProductDocument productDocument = getProductColl().find(eq("_id", "CocaCola Light 33ml")).first();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(productDocument).isNotNull();
        assertThat(response.getBody()).isEqualTo(newProduct);
        assertThat(response.getBody().getName()).isEqualTo(productDocument.getName());
        assertThat(response.getBody().getPrice()).isEqualTo(productDocument.getPrice());
    }

    @Test
    public void UpsertProduct_Should_UpdateAProduct_When_ProductExists() {
        ProductDto updatedProduct = new ProductDto("CocaCola Zero 33ml", 0.98);

        ResponseEntity<ProductDto> response = getTestRestTemplate().exchange(
                "/product/",
                POST,
                getEntity(updatedProduct),
                ProductDto.class);

        ProductDocument productDocument = getProductColl().find(eq("_id", "CocaCola Zero 33ml")).first();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(productDocument).isNotNull();
        assertThat(response.getBody()).isEqualTo(updatedProduct);
        assertThat(response.getBody().getName()).isEqualTo(productDocument.getName());
        assertThat(response.getBody().getPrice()).isEqualTo(productDocument.getPrice());
    }

    @Test
    public void DeleteProduct_Should_DeleteAProduct() {
        ProductDto expectedResponse = new ProductDto("CocaCola Zero 33ml", 0.78);

        ResponseEntity<ProductDto> response = getTestRestTemplate().exchange(
                "/product/{productName}",
                GET,
                getEntity(),
                ProductDto.class,
                Map.of("productName", "CocaCola Zero 33ml"));

        ProductDocument productDocument = getProductColl().find(eq("_id", "CocaCola Light 33ml")).first();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(productDocument).isNull();
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    public void DeleteProduct_Should_GetNotFound_When_ProductDoesNotExists() {
        ResponseEntity<String> response = getTestRestTemplate().exchange(
                "/product/{productName}",
                GET,
                getEntity(),
                String.class,
                Map.of("productName", "CocaCola Light 33ml"));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
