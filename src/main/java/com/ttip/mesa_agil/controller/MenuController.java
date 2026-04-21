package com.ttip.mesa_agil.controller;

import com.ttip.mesa_agil.dto.MenuResponse;
import com.ttip.mesa_agil.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping
    public ResponseEntity<MenuResponse> getMenu(){
        MenuResponse menuResponse = menuService.getMenu();
        return ResponseEntity.ok(menuResponse);
    }
}
