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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(100L);
        product1.setQuantity(10L);

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(200L);
        product2.setQuantity(20L);
    }

    @Test
    void findAll_ValidPageAndSize_ReturnsPagedProducts() {
        List<Product> products = Arrays.asList(product1, product2);
        Page<Product> page = new PageImpl<>(products);
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(page);

        List<Product> result = productService.findAll(0, 2);

        assertEquals(2, result.size());
        assertEquals(product1, result.get(0));
        assertEquals(product2, result.get(1));
        verify(productRepository).findAll(PageRequest.of(0, 2));
    }

    @Test
    void findAll_ZeroPageSize_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> productService.findAll(0, 0));
    }

    @Test
    void findAll_NegativePageNumber_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> productService.findAll(-1, 5));
    }

    @Test
    void findAll_EmptyPage_ReturnsEmptyList() {
        Page<Product> emptyPage = new PageImpl<>(List.of());
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        List<Product> result = productService.findAll(0, 5);

        assertTrue(result.isEmpty());
        verify(productRepository).findAll(PageRequest.of(0, 5));
    }

    @Test
    void findById_ExistingId_ReturnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Product result = productService.findById(1L);

        assertNotNull(result);
        assertEquals(product1, result);
        verify(productRepository).findById(1L);
    }

    @Test
    void findById_NonExistingId_ReturnsNull() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        Product result = productService.findById(999L);

        assertNull(result);
        verify(productRepository).findById(999L);
    }

    @Test
    void findById_NegativeId_ReturnsNull() {
        when(productRepository.findById(-1L)).thenReturn(Optional.empty());

        Product result = productService.findById(-1L);

        assertNull(result);
        verify(productRepository).findById(-1L);
    }

    // Tests for create
    @Test
    void create_ValidProduct_ReturnsId() {
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        Long result = productService.create(product1);

        assertEquals(1L, result);
        verify(productRepository).save(product1);
    }

    @Test
    void create_NullProduct_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> productService.create(null));
        verify(productRepository, never()).save(any());
    }

    @Test
    void create_ProductWithNullFields_SavesSuccessfully() {
        Product product = new Product();
        product.setId(3L);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Long result = productService.create(product);

        assertEquals(3L, result);
        verify(productRepository).save(product);
    }

    // Tests for update
    @Test
    void update_ValidProduct_ReturnsUpdatedProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        Product result = productService.update(product1);

        assertEquals(product1, result);
        verify(productRepository).save(product1);
    }

    @Test
    void update_NullProduct_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> productService.update(null));
        verify(productRepository, never()).save(any());
    }

    @Test
    void update_ProductWithUpdatedFields_SavesSuccessfully() {
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(150L);
        updatedProduct.setQuantity(15L);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.update(updatedProduct);

        assertEquals(updatedProduct, result);
        verify(productRepository).save(updatedProduct);
    }

    @Test
    void update_ProductWithNullFields_SavesSuccessfully() {
        Product product = new Product();
        product.setId(1L);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.update(product);

        assertEquals(product, result);
        verify(productRepository).save(product);
    }

    // Tests for delete
    @Test
    void delete_ExistingId_DeletesSuccessfully() {
        doNothing().when(productRepository).deleteById(1L);

        productService.delete(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void delete_NonExistingId_DoesNotThrowException() {
        doNothing().when(productRepository).deleteById(999L);

        productService.delete(999L);

        verify(productRepository).deleteById(999L);
    }

    @Test
    void delete_NegativeId_DoesNotThrowException() {
        doNothing().when(productRepository).deleteById(-1L);

        productService.delete(-1L);

        verify(productRepository).deleteById(-1L);
    }

    @Test
    void delete_ValidId_VerifiesSingleDeletion() {
        doNothing().when(productRepository).deleteById(1L);

        productService.delete(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    // Additional edge cases
    @Test
    void findAll_LargePageSize_ReturnsProducts() {
        List<Product> products = List.of(product1);
        Page<Product> page = new PageImpl<>(products);
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(page);

        List<Product> result = productService.findAll(0, 100);

        assertEquals(1, result.size());
        assertEquals(product1, result.get(0));
        verify(productRepository).findAll(PageRequest.of(0, 100));
    }

    @Test
    void create_ProductWithMaxLongId_SavesSuccessfully() {
        Product product = new Product();
        product.setId(Long.MAX_VALUE);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Long result = productService.create(product);

        assertEquals(Long.MAX_VALUE, result);
        verify(productRepository).save(product);
    }

    @Test
    void update_ProductWithMaxLongFields_SavesSuccessfully() {
        Product product = new Product();
        product.setId(1L);
        product.setPrice(Long.MAX_VALUE);
        product.setQuantity(Long.MAX_VALUE);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.update(product);

        assertEquals(product, result);
        verify(productRepository).save(product);
    }
}