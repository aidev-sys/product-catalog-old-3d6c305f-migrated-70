package com.example.productcatalog.service;

import com.example.productcatalog.messaging.ProductEventPublisher;
import com.example.productcatalog.model.Product;
import com.example.productcatalog.model.ProductNotFoundException;
import com.example.productcatalog.repository.ProductRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ProductEventPublisher eventPublisher;
    private final RabbitTemplate rabbitTemplate;

    public ProductService(ProductRepository repository, ProductEventPublisher eventPublisher, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    @Cacheable(value = "products", key = "#id")
    public Product getProduct(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Product createProduct(Product product) {
        Product saved = repository.save(product);
        eventPublisher.publishCreated(saved);
        return saved;
    }

    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        repository.deleteById(id);
        eventPublisher.publishDeleted(id);
    }

    @RabbitListener(queuesToDeclare = @org.springframework.amqp.rabbit.annotation.Queue(name = "product.created", durable = "true"))
    public void handleProductCreated(String message) {
        // Process the created product message
    }

    @RabbitListener(queuesToDeclare = @org.springframework.amqp.rabbit.annotation.Queue(name = "product.deleted", durable = "true"))
    public void handleProductDeleted(String message) {
        // Process the deleted product message
    }
}