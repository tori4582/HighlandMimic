package edu.rmit.highlandmimic.service;

import edu.rmit.highlandmimic.model.Product;
import edu.rmit.highlandmimic.model.request.ProductRequestEntity;
import edu.rmit.highlandmimic.repository.ProductRepository;
import edu.rmit.highlandmimic.repository.ToppingRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

import static edu.rmit.highlandmimic.model.mapping.ModelMappingHandlers.*;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ToppingService toppingService;
    private final TagService tagService;

    // READ operations

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> searchProductsByName(String nameQuery) {
        return productRepository.getProductsByProductNameContainsIgnoreCase(nameQuery);
    }

    // WRITE operations

    public Product createNewProduct(ProductRequestEntity reqEntity) {
        Product preparedProduct = Product.builder()
                .productName(reqEntity.getName())
                .price(reqEntity.getPrice())
                .imageUrl(reqEntity.getImageUrl())
                .description(reqEntity.getDescription())
                .upsizeOptions(reqEntity.getUpsizeOptions())
                .toppingOptions(convertListOfIdsToToppings(
                    this.toppingService.getAllToppings(),
                    reqEntity.getToppingIds()
                ))
                .tags(convertListOfIdsToTags(
                        this.tagService.getAllTags(),
                        reqEntity.getTagIds()
                ))
                .build();

        return productRepository.save(preparedProduct);
    }

    // MODIFY operations

    public Product updateExistingProduct(String id, ProductRequestEntity reqEntity) {

        Product preparedProduct = ofNullable(this.getProductById(id))
                .map(loadedEntity -> {
                    loadedEntity.setProductName(reqEntity.getName());
                    loadedEntity.setPrice(reqEntity.getPrice());
                    loadedEntity.setImageUrl(reqEntity.getImageUrl());
                    loadedEntity.setDescription(reqEntity.getDescription());
                    loadedEntity.setUpsizeOptions(reqEntity.getUpsizeOptions());
                    loadedEntity.setToppingOptions(convertListOfIdsToToppings(
                            this.toppingService.getAllToppings(),
                            reqEntity.getToppingIds()
                    ));
                    loadedEntity.setTags(convertListOfIdsToTags(
                            this.tagService.getAllTags(),
                            reqEntity.getTagIds()
                    ));

                    return loadedEntity;
                }).orElseThrow();

        return productRepository.save(preparedProduct);
    }

    @SneakyThrows
    public Product updateFieldValueOfExistingProduct(String id, String fieldName, Object newValue) {
        Product preparedProduct =  ofNullable(this.getProductById(id)).orElseThrow();

        Field preparedField = preparedProduct.getClass().getDeclaredField(fieldName);
        preparedField.setAccessible(true);
        preparedField.set(preparedProduct, newValue);

        return productRepository.save(preparedProduct);
    }

    public Object updateToppingOptions(String id, List<String> toppingIds) {
        return productRepository.findById(id)
                .map(loadedEntity -> {
                    loadedEntity.setToppingOptions(
                            convertListOfIdsToToppings(toppingService.getAllToppings(), toppingIds)
                    );

                    productRepository.save(loadedEntity);
                    return loadedEntity;
                }).orElseThrow();
    }

    public Product updateProductTags(String id, List<String> tagIds) {
        return productRepository.findById(id)
                .map(loadedEntity -> {
                    loadedEntity.setTags(
                            convertListOfIdsToTags(tagService.getAllTags(), tagIds)
                    );

                    productRepository.save(loadedEntity);
                    return loadedEntity;
                }).orElseThrow();
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