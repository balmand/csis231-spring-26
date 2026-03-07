package com.csis231.springpostgrescrud.mapper;

import com.csis231.springpostgrescrud.dto.ItemDto;
import com.csis231.springpostgrescrud.entity.Item;

public class ItemMapper {

    public static ItemDto toDto(Item item){
        return new ItemDto(item.getId(),
                item.getName(),
                item.getSerialNumber(),
                item.getCategory(),
                item.getStatus());
    }
    public static Item toEntity(ItemDto itemDto){
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getSerialNumber(),
                itemDto.getCategory(),
                itemDto.getStatus());
    }
}
