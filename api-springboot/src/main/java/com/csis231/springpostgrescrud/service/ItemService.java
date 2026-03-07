package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto);
    ItemDto getItemById(Long id);
    List<ItemDto> getAllItems();
    ItemDto updateItem(Long id, ItemDto itemDto);
    void deleteItem(Long id);
}
