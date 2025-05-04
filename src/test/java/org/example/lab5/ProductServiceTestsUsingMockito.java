/*
 * Copyright (c) 2025. Vitalii Novik
 */
package org.example.lab5;

import org.example.lab5.models.Product;
import org.example.lab5.repositories.ProductRepository;
import org.example.lab5.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTestsUsingMockito {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(100L);
        product.setQuantity(10L);
    }

    @Test
    void findAll_NegativePageSize_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> productService.findAll(0, -1));
        verify(productRepository, never()).findAll(any(PageRequest.class));
    }

    @Test
    void findAll_LargePageNumber_ReturnsEmptyList() {
        Page<Product> emptyPage = new PageImpl<>(List.of());
        when(productRepository.findAll(PageRequest.of(1000, 10))).thenReturn(emptyPage);

        List<Product> result = productService.findAll(1000, 10);

        assertTrue(result.isEmpty());
        verify(productRepository).findAll(PageRequest.of(1000, 10));
    }

    @Test
    void findById_ZeroId_ReturnsNull() {
        when(productRepository.findById(0L)).thenReturn(Optional.empty());

        Product result = productService.findById(0L);

        assertNull(result);
        verify(productRepository).findById(0L);
    }

    @Test
    void create_ProductWithZeroPrice_SavesSuccessfully() {
        Product zeroPriceProduct = new Product();
        zeroPriceProduct.setId(2L);
        zeroPriceProduct.setName("Zero Price Product");
        zeroPriceProduct.setPrice(0L);
        zeroPriceProduct.setQuantity(5L);
        when(productRepository.save(any(Product.class))).thenReturn(zeroPriceProduct);

        Long result = productService.create(zeroPriceProduct);

        assertEquals(2L, result);
        verify(productRepository).save(zeroPriceProduct);
    }

    @Test
    void create_RepositoryThrowsException_PropagatesException() {
        when(productRepository.save(any(Product.class))).thenThrow(new DataAccessException("DB Error") {});

        assertThrows(DataAccessException.class, () -> productService.create(product));
        verify(productRepository).save(product);
    }

    @Test
    void update_PartialUpdateNameOnly_SavesSuccessfully() {
        Product partialUpdate = new Product();
        partialUpdate.setId(1L);
        partialUpdate.setName("Updated Name");
        when(productRepository.save(any(Product.class))).thenReturn(partialUpdate);

        Product result = productService.update(partialUpdate);

        assertEquals("Updated Name", result.getName());
        assertNull(result.getPrice());
        assertNull(result.getQuantity());
        verify(productRepository).save(partialUpdate);
    }

    @Test
    void update_RepositoryThrowsException_PropagatesException() {
        when(productRepository.save(any(Product.class))).thenThrow(new DataAccessException("DB Error") {});

        assertThrows(DataAccessException.class, () -> productService.update(product));
        verify(productRepository).save(product);
    }

    @Test
    void delete_RepositoryThrowsException_PropagatesException() {
        doThrow(new DataAccessException("DB Error") {}).when(productRepository).deleteById(1L);

        assertThrows(DataAccessException.class, () -> productService.delete(1L));
        verify(productRepository).deleteById(1L);
    }

    @Test
    void create_MultipleSequentialCreates_VerifiesAllSaves() {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(200L);
        product2.setQuantity(20L);

        when(productRepository.save(product)).thenReturn(product);
        when(productRepository.save(product2)).thenReturn(product2);

        Long id1 = productService.create(product);
        Long id2 = productService.create(product2);

        assertEquals(1L, id1);
        assertEquals(2L, id2);
        verify(productRepository, times(2)).save(any(Product.class));
    }

    @Test
    void findAll_SmallPageSize_ReturnsSingleProduct() {
        Page<Product> singleProductPage = new PageImpl<>(List.of(product));
        when(productRepository.findAll(PageRequest.of(0, 1))).thenReturn(singleProductPage);

        List<Product> result = productService.findAll(0, 1);

        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
        verify(productRepository).findAll(PageRequest.of(0, 1));
    }
}