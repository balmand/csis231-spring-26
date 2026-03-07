package com.csis231.springpostgrescrud.controller;

import com.csis231.springpostgrescrud.dto.ItemDto;
import com.csis231.springpostgrescrud.exeption.BadRequestException;
import com.csis231.springpostgrescrud.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemController {
    private ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestBody ItemDto itemDto){
        ItemDto savedItem = itemService.createItem(itemDto);
        return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long id){
        if(id == null || id <= 0){
            throw new BadRequestException("Invalid item ID provided");
        }
        ItemDto itemDto = itemService.getItemById(id);
        return new ResponseEntity<>(itemDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems() {
        List<ItemDto> itemDtoList = itemService.getAllItems();
        return new ResponseEntity<>(itemDtoList, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long id, @RequestBody ItemDto itemDto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid item ID provided.");
        }
        ItemDto updatedItem = itemService.updateItem(id, itemDto);
        return new ResponseEntity<>(updatedItem, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid item ID provided.");
        }
        itemService.deleteItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
