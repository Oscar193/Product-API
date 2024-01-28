package com.textplus.product.repository;

import com.textplus.product.document.ProductDocument;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ProductRepository extends MongoRepository<ProductDocument, String> {
}
