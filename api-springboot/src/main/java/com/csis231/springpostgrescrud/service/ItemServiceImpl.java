package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.ItemDto;
import com.csis231.springpostgrescrud.entity.Item;
import com.csis231.springpostgrescrud.exeption.ResourceNotFoundException;
import com.csis231.springpostgrescrud.mapper.ItemMapper;
import com.csis231.springpostgrescrud.repository.ItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService{
    private ItemRepository itemRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto) {
        Item item = ItemMapper.toEntity(itemDto);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toDto(savedItem);
    }

    @Override
    public ItemDto getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with given id " + id));
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Long id, ItemDto itemDto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with given id " + id));
        item.setName(itemDto.getName());
        item.setCategory(itemDto.getCategory());
        item.setStatus(itemDto.getStatus());
        Item updatedItem = itemRepository.save(item);
        return ItemMapper.toDto(updatedItem);
    }

    @Override
    public void deleteItem(Long id) {
        if(!itemRepository.existsById(id)){
            throw new ResourceNotFoundException("Item not found with given id " + id);
        }
        itemRepository.deleteById(id);
    }
}
