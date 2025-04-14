package org.example.lab5.controllers;

import org.example.lab5.models.Product;
import org.example.lab5.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    private final ProductService productService;

    @Autowired
    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public Long create(@RequestBody Product product) {
        return this.productService.create(product);
    }

    @GetMapping
    public List<Product> findAll(
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        return this.productService.findAll(pageNumber, pageSize);
    }

    @GetMapping("{id}")
    public Product findById(@PathVariable long id) {
        return this.productService.findById(id);
    }

    @PutMapping("{id}")
    public Product update(@PathVariable long id, @RequestBody Product product) {
        product.setId(id);
        return this.productService.update(product);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        this.productService.delete(id);
    }
}
