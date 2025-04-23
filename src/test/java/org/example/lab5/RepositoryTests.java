/*
 * Copyright (c) 2025. Vitalii Novik
 */

package org.example.lab5;

import org.example.lab5.models.Product;
import org.example.lab5.repositories.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RepositoryTests {
    @Autowired
    private ProductRepository underTest;

    @BeforeEach
    void setUp() {
        Product laptop = new Product();
        laptop.setName("Laptop");
        laptop.setPrice(999L);
        laptop.setQuantity(10L);

        Product phone = new Product();
        phone.setName("Phone");
        phone.setPrice(499L);
        phone.setQuantity(20L);

        Product tablet = new Product();
        tablet.setName("Tablet");
        tablet.setPrice(299L);
        tablet.setQuantity(15L);

        underTest.saveAll(List.of(laptop, phone, tablet));
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldSaveProductAndGenerateId() {
        Product watch = new Product();
        watch.setName("Watch");
        watch.setPrice(199L);
        watch.setQuantity(30L);

        Product savedProduct = underTest.save(watch);

        assertNotNull(savedProduct.getId());
        assertEquals("Watch", savedProduct.getName());
        assertEquals(199L, savedProduct.getPrice());
        assertEquals(30L, savedProduct.getQuantity());
    }

    @Test
    void shouldFindProductById() {
        Product monitor = new Product();
        monitor.setName("Monitor");
        monitor.setPrice(249L);
        monitor.setQuantity(5L);
        Product savedMonitor = underTest.save(monitor);

        Optional<Product> foundProduct = underTest.findById(savedMonitor.getId());

        assertTrue(foundProduct.isPresent());
        assertEquals("Monitor", foundProduct.get().getName());
        assertEquals(249L, foundProduct.get().getPrice());
        assertEquals(5L, foundProduct.get().getQuantity());
    }

    @Test
    void shouldReturnEmptyWhenIdNotFound() {
        Optional<Product> foundProduct = underTest.findById(999L);

        assertFalse(foundProduct.isPresent());
    }

    @Test
    void shouldFindAllProducts() {
        List<Product> products = underTest.findAll();

        assertEquals(3, products.size());
    }

    @Test
    void shouldUpdateProduct() {
        Product speaker = new Product();
        speaker.setName("Speaker");
        speaker.setPrice(99L);
        speaker.setQuantity(25L);
        Product savedSpeaker = underTest.save(speaker);

        savedSpeaker.setPrice(149L);
        savedSpeaker.setQuantity(20L);
        underTest.save(savedSpeaker);
        Optional<Product> updatedProduct = underTest.findById(savedSpeaker.getId());

        assertTrue(updatedProduct.isPresent());
        assertEquals(149L, updatedProduct.get().getPrice());
        assertEquals(20L, updatedProduct.get().getQuantity());
        assertEquals("Speaker", updatedProduct.get().getName());
    }

    @Test
    void shouldDeleteProductById() {
        Product keyboard = new Product();
        keyboard.setName("Keyboard");
        keyboard.setPrice(79L);
        keyboard.setQuantity(50L);
        Product savedKeyboard = underTest.save(keyboard);

        underTest.deleteById(savedKeyboard.getId());
        Optional<Product> deletedProduct = underTest.findById(savedKeyboard.getId());

        assertFalse(deletedProduct.isPresent());
    }

    @Test
    void shouldDeleteAllProducts() {
        underTest.deleteAll();
        List<Product> products = underTest.findAll();

        assertTrue(products.isEmpty());
    }

    @Test
    void shouldSaveMultipleProducts() {
        underTest.deleteAll();
        Product mouse = new Product();
        mouse.setName("Mouse");
        mouse.setPrice(29L);
        mouse.setQuantity(100L);

        Product headset = new Product();
        headset.setName("Headset");
        headset.setPrice(59L);
        headset.setQuantity(40L);

        underTest.saveAll(List.of(mouse, headset));
        List<Product> products = underTest.findAll();

        assertEquals(2, products.size());
    }

    @Test
    void shouldCountProducts() {
        long count = underTest.count();

        assertEquals(3, count);
    }

    @Test
    void shouldCheckIfProductExistsById() {
        Product camera = new Product();
        camera.setName("Camera");
        camera.setPrice(399L);
        camera.setQuantity(8L);
        Product savedCamera = underTest.save(camera);

        boolean exists = underTest.existsById(savedCamera.getId());
        boolean notExists = underTest.existsById(999L);

        assertTrue(exists);
        assertFalse(notExists);
    }
}
