package edu.rmit.highlandmimic.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Document("product-catalogues")
public class ProductCatalogue {

    @Id
    private String productCatalogueId;

    private String productCatalogueName;
    private String description;
    private String imageUrl;

    @DBRef
    private List<ProductCatalogue> subCatalogues;
}
