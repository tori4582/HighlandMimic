package edu.rmit.highlandmimic.service;

import edu.rmit.highlandmimic.model.Topping;
import edu.rmit.highlandmimic.model.request.ToppingRequestEntity;
import edu.rmit.highlandmimic.repository.ToppingRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class ToppingService {

    private final ToppingRepository toppingRepository;

    // READ operations

    public List<Topping> getAllToppings() {
        return toppingRepository.findAll();
    }

    public Topping getToppingById(String id) {
        return toppingRepository.findById(id).orElse(null);
    }

    public List<Topping> searchToppingsByName(String nameQuery) {
        return toppingRepository.getToppingsByToppingNameContains(nameQuery);
    }

    // WRITE operations

    public Topping createNewTopping(ToppingRequestEntity reqEntity) {
        Topping preparedTopping = Topping.builder()
                .toppingName(reqEntity.getName())
                .description(reqEntity.getDescription())
                .imageUrl(reqEntity.getImageUrl())
                .pricePerService(reqEntity.getPricePerService())
                .build();

        return toppingRepository.save(preparedTopping);
    }

    // MODIFY operations

    public Topping updateExistingTopping(String id, ToppingRequestEntity reqEntity) {

        Topping preparedTopping = ofNullable(this.getToppingById(id))
                .map(loadedEntity -> {
                    loadedEntity.setToppingName(reqEntity.getName());
                    loadedEntity.setDescription(reqEntity.getDescription());
                    loadedEntity.setImageUrl(reqEntity.getImageUrl());
                    loadedEntity.setPricePerService(reqEntity.getPricePerService());

                    return loadedEntity;
                }).orElseThrow();

        return toppingRepository.save(preparedTopping);
    }

    @SneakyThrows
    public Topping updateFieldValueOfExistingTopping(String id, String fieldName, Object newValue) {
        Topping preparedTopping =  ofNullable(this.getToppingById(id)).orElseThrow();

        Field preparedField = preparedTopping.getClass().getDeclaredField(fieldName);
        preparedField.setAccessible(true);
        preparedField.set(preparedTopping, (fieldName.equalsIgnoreCase("priceperservice"))
                ? Long.valueOf(newValue.toString())
                : newValue
        );

        return toppingRepository.save(preparedTopping);
    }


    // DELETE operations

    public Topping removeToppingById(String id) {
        return toppingRepository.findById(id)
                .map(loadedEntity -> {
                    toppingRepository.delete(loadedEntity);
                    return loadedEntity;
                }).orElseThrow();
    }

    public long removeAllToppings() {
        long quantity = toppingRepository.count();
        toppingRepository.deleteAll();
        return quantity;
    }

}