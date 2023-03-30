package edu.rmit.highlandmimic.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
@Builder
@Document("product-catalogues")
public class ProductCatalogue {

    @Id
    private String productCatalogueId;

    private String productCatalogueName;
    private String description;
    private String imageUrl;

//    @DBRef
//    private List<ProductCatalogue> subCatalogues;

    private List<String> associatedProductIds;
}
