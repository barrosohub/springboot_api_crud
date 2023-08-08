package com.example.springboot.repositories;

import com.example.springboot.models.ProductModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, UUID> {
    @NotNull Optional<ProductModel> findById(@NotNull UUID id);
}