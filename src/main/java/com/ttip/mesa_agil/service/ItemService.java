package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.requests.BaseItemRequest;
import com.ttip.mesa_agil.dto.requests.CreateItemRequest;
import com.ttip.mesa_agil.dto.requests.UpdateItemRequest;
import com.ttip.mesa_agil.exception.ResourceNotFoundException;
import com.ttip.mesa_agil.exception.ValidationFailedException;
import com.ttip.mesa_agil.model.FoodCategory;
import com.ttip.mesa_agil.model.Item;
import com.ttip.mesa_agil.repository.FoodCategoryRepository;
import com.ttip.mesa_agil.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final FoodCategoryRepository foodCategoryRepository;
    private final FileStorageService fileStorageService;

    public Item create(CreateItemRequest request, MultipartFile imageFile) throws IOException {
        String finalImageUrl;

        boolean hasFile = imageFile != null && !imageFile.isEmpty();
        boolean hasUrl = request.getImageUrl() != null && !request.getImageUrl().isBlank();

        if (!hasFile && !hasUrl) {
            throw new IllegalArgumentException(
                    "Debe proporcionar una imagen o una URL."
            );
        }

        if (hasFile && hasUrl) {
            throw new IllegalArgumentException(
                    "Debe enviar una imagen o una URL, pero no ambas."
            );
        }

        validate(request);

        if (hasFile) {
            finalImageUrl = fileStorageService.save(imageFile);
        } else {
            validateUrl(request.getImageUrl());
            finalImageUrl = request.getImageUrl();
        }

        FoodCategory category = foodCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        Item item = new Item();

        item.setName(request.getName().trim());
        item.setDescription(request.getDescription().trim());
        item.setImageUrl(finalImageUrl.trim());
        item.setPrice(request.getPrice());
        item.setFoodCategory(category);
        item.setActive(true);

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
        if (request.getActive() != null) {
            item.setActive(request.getActive());
        }

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

    private void validateUrl(String imageUrl) {
        try {

            URI uri = new URI(imageUrl);

            if (uri.getScheme() == null ||
                    (!uri.getScheme().equals("http") &&
                            !uri.getScheme().equals("https"))) {

                throw new ValidationFailedException("URL inválida.");
            }

        } catch (URISyntaxException e) {
            throw new ValidationFailedException("URL inválida.");
        }
    }

    private void validate(BaseItemRequest request) {

        if (request.getName() == null || request.getName().trim().isBlank()) {
            throw new ValidationFailedException("Name is required");
        }

        if (request.getDescription() == null || request.getDescription().trim().isBlank()) {
            throw new ValidationFailedException("Description is required");
        }

        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationFailedException("Price must be greater than zero");
        }

        if (request.getCategoryId() == null) {
            throw new ValidationFailedException("Category is required");
        }

    }
}
