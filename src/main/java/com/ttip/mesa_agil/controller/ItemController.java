package com.ttip.mesa_agil.controller;

import com.ttip.mesa_agil.dto.CreateItemRequest;
import com.ttip.mesa_agil.dto.ItemResponse;
import com.ttip.mesa_agil.dto.UpdateItemRequest;
import com.ttip.mesa_agil.mapper.ItemMapper;
import com.ttip.mesa_agil.model.Item;
import com.ttip.mesa_agil.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponse> create(
            @RequestBody CreateItemRequest request) {

        Item item = itemService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ItemMapper.toResponse(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateItemRequest request) {

        Item item = itemService.update(id, request);

        return ResponseEntity.ok(
                ItemMapper.toResponse(item)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        itemService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> findAll() {

        List<ItemResponse> response = itemService.findAll()
                .stream()
                .map(ItemMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }
}