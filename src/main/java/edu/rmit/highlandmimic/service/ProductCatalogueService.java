package edu.rmit.highlandmimic.service;

import edu.rmit.highlandmimic.model.ProductCatalogue;
import edu.rmit.highlandmimic.model.mapping.ModelMappingHandlers;
import edu.rmit.highlandmimic.model.request.ProductCatalogueRequestEntity;
import edu.rmit.highlandmimic.repository.ProductCatalogueRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

import static edu.rmit.highlandmimic.model.mapping.ModelMappingHandlers.convertListOfIdsToCatalogues;
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
        return productCatalogueRepository.getProductCataloguesByProductCatalogueNameContainsIgnoreCase(nameQuery);
    }

    // WRITE operations

    public ProductCatalogue createNewProductCatalogue(ProductCatalogueRequestEntity reqEntity) {
        ProductCatalogue preparedProductCatalogue = ProductCatalogue.builder()
                .productCatalogueName(reqEntity.getName())
                .description(reqEntity.getDescription())
                .imageUrl(reqEntity.getImageUrl())
                .subCatalogues(
                        convertListOfIdsToCatalogues(this.getAllProductCatalogues(), reqEntity.getSubCatalogueIds())
                )
                .build();

        return productCatalogueRepository.save(preparedProductCatalogue);
    }

    // MODIFY operations

    public ProductCatalogue updateExistingProductCatalogue(String id, ProductCatalogueRequestEntity reqEntity) {

        ProductCatalogue preparedProductCatalogue = ofNullable(this.getProductCatalogueById(id))
                .map(loadedEntity -> {
                    loadedEntity.setProductCatalogueName(reqEntity.getName());
                    loadedEntity.setDescription(reqEntity.getDescription());
                    loadedEntity.setImageUrl(reqEntity.getImageUrl());
                    loadedEntity.setSubCatalogues(
                            convertListOfIdsToCatalogues(this.getAllProductCatalogues(), reqEntity.getSubCatalogueIds())
                    );

                    return loadedEntity;
                }).orElseThrow();

        return productCatalogueRepository.save(preparedProductCatalogue);
    }

    @SneakyThrows
    public ProductCatalogue updateFieldValueOfExistingProductCatalogue(String id, String fieldName, Object newValue) {

        if (fieldName.equalsIgnoreCase("subcatalogues")) {
            throw new UnsupportedOperationException("In order to update 'subCatalogues' field, use the '/{id}/sub-catalogues' endpoint instead.");
        }

        ProductCatalogue preparedProductCatalogue =  ofNullable(this.getProductCatalogueById(id)).orElseThrow();

        Field preparedField = preparedProductCatalogue.getClass().getDeclaredField(fieldName);
        preparedField.setAccessible(true);
        preparedField.set(preparedProductCatalogue, newValue);

        return productCatalogueRepository.save(preparedProductCatalogue);
    }

    public ProductCatalogue updateSubCatalogues(String id, List<String> subCatalogues) {
        return productCatalogueRepository.findById(id)
              .map(loadedEntity -> {
                    loadedEntity.setSubCatalogues(
                            convertListOfIdsToCatalogues(this.getAllProductCatalogues(), subCatalogues)
                    );

                    productCatalogueRepository.save(loadedEntity);
                    return loadedEntity;
                }).orElseThrow();
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