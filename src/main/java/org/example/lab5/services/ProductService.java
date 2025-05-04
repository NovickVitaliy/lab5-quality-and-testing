package org.example.lab5.services;

import org.example.lab5.models.Product;
import org.example.lab5.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll(int pageNumber, int pageSize) {
        return this.productRepository.findAll(PageRequest.of(pageNumber, pageSize)).toList();
    }

    public Product findById(long id) {
        return this.productRepository.findById(id).orElse(null);
    }

    public Long create(Product product) {
        if (product == null) {
            throw new NullPointerException("Product cannot be null");
        }
        return this.productRepository.save(product).getId();
    }

    public Product update(Product product) {
        if (product == null) {
            throw new NullPointerException("Product cannot be null");
        }
        return this.productRepository.save(product);
    }

    public void delete(long id) {
        this.productRepository.deleteById(id);
    }
}
