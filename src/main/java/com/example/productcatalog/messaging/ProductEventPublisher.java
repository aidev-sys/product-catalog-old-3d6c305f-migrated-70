package com.example.productcatalog.messaging;

import com.example.productcatalog.model.Product;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductEventPublisher {

    private static final String QUEUE = "product-events";
    private final RabbitTemplate rabbitTemplate;

    public ProductEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCreated(Product product) {
        rabbitTemplate.convertAndSend(QUEUE, "CREATED:" + product.getId() + ":" + product.getName());
    }

    public void publishDeleted(Long id) {
        rabbitTemplate.convertAndSend(QUEUE, "DELETED:" + id);
    }
}