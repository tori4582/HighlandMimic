package edu.rmit.highlandmimic.controller;

import edu.rmit.highlandmimic.model.ProductCatalogue;
import edu.rmit.highlandmimic.model.request.CouponRequestEntity;
import edu.rmit.highlandmimic.service.ProductCatalogueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static edu.rmit.highlandmimic.common.ControllerUtils.controllerWrapper;

@RestController
@RequestMapping("/product-catalogues")
@RequiredArgsConstructor
public class ProductCatalogueController {

    private final ProductCatalogueService productCatalogueService;

    // READ operations

    @GetMapping
    public ResponseEntity<List<ProductCatalogue>> getAllProductCatalogues() {
        return ResponseEntity.ok(productCatalogueService.getAllProductCatalogues());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCatalogue> getProductCatalogueById(@PathVariable String id) {
        return ResponseEntity.ok(productCatalogueService.getProductCatalogueById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductCatalogue>> searchProductCataloguesByName(@RequestParam String q) {
        return ResponseEntity.ok(productCatalogueService.searchProductCataloguesByName(q));
    }

    // WRITE operation

    @PostMapping
    public ResponseEntity<?> createNewProductCatalogue(@RequestBody CouponRequestEntity.ProductCatalogueRequestEntity reqEntity) {
        return controllerWrapper(() -> productCatalogueService.createNewProductCatalogue(reqEntity));
    }

    // MODIFY operation

    @PostMapping("/{id}")
    public ResponseEntity<?> updateExistingProductCatalogue(@PathVariable String id, @RequestBody CouponRequestEntity.ProductCatalogueRequestEntity reqEntity) {
        return controllerWrapper(() -> productCatalogueService.updateExistingProductCatalogue(id, reqEntity));
    }

    @PostMapping("/E/{id}/{fieldName}")
    public ResponseEntity<?> updateFieldValueOfExistingProductCatalogue(@PathVariable String id,
                                                          @PathVariable String fieldName,
                                                          @RequestBody Object newValue) {
        return controllerWrapper(() -> productCatalogueService.updateFieldValueOfExistingProductCatalogue(id, fieldName, newValue));
    }

    // DELETE operation

    @DeleteMapping()
    public ResponseEntity<Long> removeAllProductCatalogues() {
        return ResponseEntity.ok(productCatalogueService.removeAllProductCatalogues());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeProductCatalogueById(@PathVariable String id) {
        return controllerWrapper(() -> productCatalogueService.removeProductCatalogueById(id));
    }

}
