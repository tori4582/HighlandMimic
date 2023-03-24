package edu.rmit.highlandmimic.service;

import edu.rmit.highlandmimic.model.Product;
import edu.rmit.highlandmimic.model.request.ProductRequestEntity;
import edu.rmit.highlandmimic.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // READ operations

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> searchProductsByName(String nameQuery) {
        return productRepository.getProductsByProductNameContains(nameQuery);
    }

    // WRITE operations

    public Product createNewProduct(ProductRequestEntity reqEntity) {
        Product preparedProduct = Product.builder()
                .build();

        return productRepository.save(preparedProduct);
    }

    // MODIFY operations

    public Product updateExistingProduct(String id, ProductRequestEntity reqEntity) {

        Product preparedProduct = ofNullable(this.getProductById(id))
                .map(loadedEntity -> {
                    Product ProductObj = Product.builder()
                            .build();


                    return ProductObj;
                }).orElseThrow();

//        return productRepository.update(preparedProduct);
        return null;
    }

    public Product updateFieldValueOfExistingProduct(String id, String fieldName, Object newValue) {
        Product preparedProduct =  ofNullable(this.getProductById(id))
                .map(loadedEntity -> {
                    Product ProductObj = Product.builder()
                            .build();


                    return ProductObj;
                }).orElseThrow();

//        return productRepository.update(preparedProduct);
        return null;
    }


    // DELETE operations

    public Product removeProductById(String id) {
        return productRepository.findById(id)
                .map(loadedEntity -> {
                    productRepository.delete(loadedEntity);
                    return loadedEntity;
                }).orElseThrow();
    }

    public long removeAllProducts() {
        long quantity = productRepository.count();
        productRepository.deleteAll();
        return quantity;
    }

}