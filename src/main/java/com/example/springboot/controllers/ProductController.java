package com.example.springboot.controllers;

import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.UUID;

@RestController
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @GetMapping("/status")
    public ResponseEntity<String> info() {
        return ResponseEntity.ok("Application Running...");
    }

    @PostMapping("/products")
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto) {
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDto, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }


    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts() {


        List<ProductModel> productModelList = productRepository.findAll();
        if (!productModelList.isEmpty()) {
            for (ProductModel product : productModelList) {
                UUID id = product.getIdProduct();
                product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(productModelList);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id") UUID id) {
        var productModel = productRepository.findById(id);
        if (productModel.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        productModel.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("List of Products"));
        return ResponseEntity.status(HttpStatus.OK).body(productModel);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductModel> updateProduct(@PathVariable(value = "id") UUID id,
                                                      @RequestBody @Valid ProductRecordDto productRecordDto) {
        var productModel = productRepository.findById(id);
        if (productModel.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        BeanUtils.copyProperties(productRecordDto, productModel.get());
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel.get()));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id) {
        var productModel = productRepository.findById(id);
        if (productModel.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        productRepository.delete(productModel.get());
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted!");
    }

}
