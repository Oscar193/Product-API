package com.textplus.product.repository;

import com.textplus.product.document.OrderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface OrderRepository extends MongoRepository<OrderDocument, String> {

    List<OrderDocument> findByCreationDateTimeBetween(Instant startCreationDateTime, Instant endCreationDateTime);
}
