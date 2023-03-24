package edu.rmit.highlandmimic.service;

import edu.rmit.highlandmimic.model.Store;
import edu.rmit.highlandmimic.model.request.StoreRequestEntity;
import edu.rmit.highlandmimic.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public List<Store> getStoresByAddress4(String address4) {
        return storeRepository.getStoreByAddress4(address4);
    }

    public Store getStoreById(String id) {
        return storeRepository.findById(id)
                .orElse(null);
    }

    public List<Store> searchStoresByName(String nameQuery) {
        return storeRepository.getStoreByStoreNameContains(nameQuery);
    }

    public Store createNewStore(StoreRequestEntity reqEntity) {
        Store preparedStore = Store.builder()
                .storeName(reqEntity.getName())
                .address1(reqEntity.getAddress1())
                .address2(reqEntity.getAddress2())
                .address3(reqEntity.getAddress3())
                .address4(reqEntity.getAddress4())
                .build();

        return storeRepository.save(preparedStore);
    }

    // MODIFY operations

    public Store updateExistingStore(String id, StoreRequestEntity reqEntity) {

        Store preparedStore = ofNullable(this.getStoreById(id))
                .map(loadedEntity -> {
                    Store storeObj = Store.builder()
                            .build();

                    return storeObj;
                }).orElseThrow();

        return storeRepository.save(preparedStore);
    }

    public Store updateFieldValueOfExistingStore(String id, String fieldName, Object newValue) {
        ofNullable(this.getStoreById(id))
                .map(loadedEntity -> {

                    return null;
                }).orElseThrow();

        return null;
    }

    // DELETE operations

    public Store removeStoreById(String id) {
        return storeRepository.findById(id)
                .map(project -> {
                    storeRepository.delete(project);
                    return project;
                }).orElseThrow();
    }

    public long removeAllStores() {
        long quantity = storeRepository.count();
        storeRepository.deleteAll();
        return quantity;
    }

}
