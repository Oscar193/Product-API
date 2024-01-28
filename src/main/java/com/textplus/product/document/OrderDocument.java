package com.textplus.product.document;

import com.textplus.product.dto.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "order")
public class OrderDocument {

    @BsonId
    @MongoId
    private String id;
    private List<ProductDocument> products;
    private Instant creationDateTime;
    private OrderStatusEnum status;
}
