package edu.rmit.highlandmimic.service;

import edu.rmit.highlandmimic.model.ProductCatalogue;
import edu.rmit.highlandmimic.model.request.CouponRequestEntity;
import edu.rmit.highlandmimic.repository.ProductCatalogueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class ProductCatalogueService {

    private final ProductCatalogueRepository productCatalogueRepository;

    // READ operations

    public List<ProductCatalogue> getAllProductCatalogues() {
        return productCatalogueRepository.findAll();
    }

    public ProductCatalogue getProductCatalogueById(String id) {
        return productCatalogueRepository.findById(id).orElse(null);
    }

    public List<ProductCatalogue> searchProductCataloguesByName(String nameQuery) {
        return productCatalogueRepository.getProductCataloguesByNameContains(nameQuery);
    }

    // WRITE operations

    public ProductCatalogue createNewProductCatalogue(CouponRequestEntity.ProductCatalogueRequestEntity reqEntity) {
        ProductCatalogue preparedProductCatalogue = ProductCatalogue.builder()
                .build();

        return productCatalogueRepository.save(preparedProductCatalogue);
    }

    // MODIFY operations

    public ProductCatalogue updateExistingProductCatalogue(String id, CouponRequestEntity.ProductCatalogueRequestEntity reqEntity) {

        ProductCatalogue preparedProductCatalogue = ofNullable(this.getProductCatalogueById(id))
                .map(loadedEntity -> {
                    ProductCatalogue ProductCatalogueObj = ProductCatalogue.builder()
                            .build();


                    return ProductCatalogueObj;
                }).orElseThrow();

//        return productCatalogueRepository.update(preparedProductCatalogue);
        return null;
    }

    public ProductCatalogue updateFieldValueOfExistingProductCatalogue(String id, String fieldName, Object newValue) {
        ProductCatalogue preparedProductCatalogue =  ofNullable(this.getProductCatalogueById(id))
                .map(loadedEntity -> {
                    ProductCatalogue ProductCatalogueObj = ProductCatalogue.builder()
                            .build();


                    return ProductCatalogueObj;

                }).orElseThrow();

//        return productCatalogueRepository.update(preparedProductCatalogue);
        return null;
    }

    // DELETE operations

    public ProductCatalogue removeProductCatalogueById(String id) {
        return productCatalogueRepository.findById(id)
                .map(loadedEntity -> {
                    productCatalogueRepository.delete(loadedEntity);
                    return loadedEntity;
                }).orElseThrow();
    }

    public long removeAllProductCatalogues() {
        long quantity = productCatalogueRepository.count();
        productCatalogueRepository.deleteAll();
        return quantity;
    }

}