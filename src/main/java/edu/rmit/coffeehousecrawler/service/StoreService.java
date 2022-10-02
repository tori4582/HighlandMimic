package edu.rmit.coffeehousecrawler.service;

import edu.rmit.coffeehousecrawler.model.Store;
import edu.rmit.coffeehousecrawler.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public List<Store> getAllStores() {
        return storeRepository.fetchAllStores();
    }
}
