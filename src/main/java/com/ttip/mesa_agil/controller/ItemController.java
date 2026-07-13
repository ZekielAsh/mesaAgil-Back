package com.ttip.mesa_agil.controller;

import com.ttip.mesa_agil.dto.requests.CreateItemRequest;
import com.ttip.mesa_agil.dto.responses.ItemResponse;
import com.ttip.mesa_agil.dto.requests.UpdateItemRequest;
import com.ttip.mesa_agil.mapper.ItemMapper;
import com.ttip.mesa_agil.model.Item;
import com.ttip.mesa_agil.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemResponse> create(
            @ModelAttribute CreateItemRequest request,
            @RequestPart(required = false)MultipartFile imageFile) throws IOException {

        Item item = itemService.create(request, imageFile);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ItemMapper.toResponse(item));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value ="/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemResponse> update(
            @PathVariable Long id,
            @ModelAttribute UpdateItemRequest request,
            @RequestPart(required = false)MultipartFile imageFile) throws IOException {

        Item item = itemService.update(id, request, imageFile);

        return ResponseEntity.ok(
                ItemMapper.toResponse(item)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        itemService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ItemResponse>> findAll() {

        List<ItemResponse> response = itemService.findAll()
                .stream()
                .map(ItemMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }
}