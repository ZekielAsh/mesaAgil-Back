package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.BaseItemRequest;
import com.ttip.mesa_agil.dto.CreateItemRequest;
import com.ttip.mesa_agil.dto.UpdateItemRequest;
import com.ttip.mesa_agil.exception.ResourceNotFoundException;
import com.ttip.mesa_agil.exception.ValidationFailedException;
import com.ttip.mesa_agil.model.FoodCategory;
import com.ttip.mesa_agil.model.Item;
import com.ttip.mesa_agil.repository.FoodCategoryRepository;
import com.ttip.mesa_agil.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final FoodCategoryRepository foodCategoryRepository;

    public Item create(CreateItemRequest request) {

        validate(request);

        FoodCategory category = foodCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        Item item = new Item();

        item.setName(request.getName().trim());
        item.setDescription(request.getDescription().trim());
        item.setImageUrl(request.getImageUrl().trim());
        item.setPrice(request.getPrice());
        item.setFoodCategory(category);

        return itemRepository.save(item);
    }

    public Item update(Long id, UpdateItemRequest request) {

        validate(request);

        Item item = itemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Item not found"));

        FoodCategory category = foodCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        item.setName(request.getName().trim());
        item.setDescription(request.getDescription().trim());
        item.setImageUrl(request.getImageUrl().trim());
        item.setPrice(request.getPrice());
        item.setFoodCategory(category);

        return itemRepository.save(item);
    }

    public void delete(Long id) {

        Item item = itemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Item not found"));

        itemRepository.delete(item);
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    private void validate(BaseItemRequest request) {

        if (request.getName() == null || request.getName().trim().isBlank()) {
            throw new ValidationFailedException("Name is required");
        }

        if (request.getDescription() == null || request.getDescription().trim().isBlank()) {
            throw new ValidationFailedException("Description is required");
        }

        if (request.getImageUrl() == null || request.getImageUrl().trim().isBlank()) {
            throw new ValidationFailedException("Image URL is required");
        }

        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationFailedException("Price must be greater than zero");
        }

        if (request.getCategoryId() == null) {
            throw new ValidationFailedException("Category is required");
        }
    }
}