package com.example.demo.repository;

import com.example.demo.model.Product;
import com.example.demo.model.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository

public interface ProductImageRepository extends JpaRepository<ProductImages, Long> {
}

