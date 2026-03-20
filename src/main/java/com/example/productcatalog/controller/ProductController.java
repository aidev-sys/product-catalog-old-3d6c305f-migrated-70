package com.example.productcatalog.controller;

import com.example.productcatalog.model.Product;
import com.example.productcatalog.service.ProductService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;
    private final RabbitTemplate rabbitTemplate;

    public ProductController(ProductService service, RabbitTemplate rabbitTemplate) {
        this.service = service;
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping
    public List<Product> list() {
        return service.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) {
        return service.getProduct(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product product) {
        rabbitTemplate.convertAndSend("product.created", product);
        return service.createProduct(product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteProduct(id);
    }

    @RabbitListener(queuesToDeclare = @Queue(name = "product.created", durable = "true"))
    public void handleProductCreated(Product product) {
        // Handle product created event
    }
}