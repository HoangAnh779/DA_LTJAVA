package com.example.demo.service;

import com.example.demo.model.ProductImages;
import com.example.demo.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageService {

    private final ProductImageRepository productImageRepository;

    public List<ProductImages> getAllProductImages() {

        return productImageRepository.findAll();
    }

    public void addProductImage(ProductImages productImage) {

        productImageRepository.save(productImage);
    }
}

