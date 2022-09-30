package edu.rmit.coffeehousecrawler.service;

import edu.rmit.coffeehousecrawler.model.Product;
import edu.rmit.coffeehousecrawler.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.Element;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.fetchAllProducts();
    }

}
