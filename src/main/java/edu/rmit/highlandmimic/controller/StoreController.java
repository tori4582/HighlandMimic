package edu.rmit.highlandmimic.controller;

import edu.rmit.highlandmimic.model.Store;
import edu.rmit.highlandmimic.model.request.StoreRequestEntity;
import edu.rmit.highlandmimic.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static edu.rmit.highlandmimic.common.ControllerUtils.controllerWrapper;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    // READ operations

    @GetMapping
    public ResponseEntity<List<Store>> getAllStores() {
        return ResponseEntity.ok(storeService.getAllStores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Store> getStoreById(@PathVariable String id) {
        return ResponseEntity.ok(storeService.getStoreById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Store>> searchStoreByName(@RequestParam String q) {
        return ResponseEntity.ok(storeService.searchStoresByName(q));
    }

    // WRITE operation

    @PostMapping
    public ResponseEntity<?> createNewStore(@RequestBody StoreRequestEntity reqEntity) {
        return controllerWrapper(() -> storeService.createNewStore(reqEntity));
    }

    // MODIFY operation

    @PostMapping("/{id}")
    public ResponseEntity<?> updateExistingStore(@PathVariable String id, @RequestBody StoreRequestEntity reqEntity) {
        return controllerWrapper(() -> storeService.updateExistingStore(id, reqEntity));
    }

    @PostMapping("/{id}/{fieldName}")
    public ResponseEntity<?> updateFieldValueOfExistingStore(@PathVariable String id,
                                                           @PathVariable String fieldName,
                                                           @RequestBody Object newValue) {
        return controllerWrapper(() -> storeService.updateFieldValueOfExistingStore(id, fieldName, newValue));
    }

    // DELETE operation

    @DeleteMapping()
    public ResponseEntity<Long> removeAllStores() {
        return ResponseEntity.ok(storeService.removeAllStores());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeStoreById(@PathVariable String id) {
        return controllerWrapper(() -> storeService.removeStoreById(id));
    }

}
