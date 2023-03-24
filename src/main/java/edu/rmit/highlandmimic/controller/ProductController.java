package edu.rmit.highlandmimic.controller;

import edu.rmit.highlandmimic.model.Product;
import edu.rmit.highlandmimic.model.request.ProductRequestEntity;
import edu.rmit.highlandmimic.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static edu.rmit.highlandmimic.common.ControllerUtils.controllerWrapper;

@RestController
@RequestMapping("/Product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // READ operations

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam String q) {
        return ResponseEntity.ok(productService.searchProductsByName(q));
    }

    // WRITE operation

    @PostMapping
    public ResponseEntity<?> createNewProduct(@RequestBody ProductRequestEntity reqEntity) {
        return controllerWrapper(() -> productService.createNewProduct(reqEntity));
    }

    // MODIFY operation

    @PostMapping("/{id}")
    public ResponseEntity<?> updateExistingProduct(@PathVariable String id, @RequestBody ProductRequestEntity reqEntity) {
        return controllerWrapper(() -> productService.updateExistingProduct(id, reqEntity));
    }

    @PostMapping("/E/{id}/{fieldName}")
    public ResponseEntity<?> updateFieldValueOfExistingProduct(@PathVariable String id,
                                                          @PathVariable String fieldName,
                                                          @RequestBody Object newValue) {
        return controllerWrapper(() -> productService.updateFieldValueOfExistingProduct(id, fieldName, newValue));
    }

    // DELETE operation

    @DeleteMapping()
    public ResponseEntity<Long> removeAllProducts() {
        return ResponseEntity.ok(productService.removeAllProducts());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeProductById(@PathVariable String id) {
        return controllerWrapper(() -> productService.removeProductById(id));
    }

}