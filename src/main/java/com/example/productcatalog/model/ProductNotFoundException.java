package com.example.productcatalog.model;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductNotFoundException extends RuntimeException {

    private static final String PRODUCT_NOT_FOUND_QUEUE = "product.not.found.queue";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public ProductNotFoundException(Long id) {
        super("Product not found: " + id);
        rabbitTemplate.convertAndSend(PRODUCT_NOT_FOUND_QUEUE, this);
    }

    @RabbitListener(queuesToDeclare = @org.springframework.amqp.rabbit.annotation.Queue(name = PRODUCT_NOT_FOUND_QUEUE, durable = "true"))
    public void handleProductNotFound(ProductNotFoundException exception) {
        // Handle the product not found event
        System.out.println("Handling product not found: " + exception.getMessage());
    }
}