package com.csis231.springpostgrescrud.dto;

import com.csis231.springpostgrescrud.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private Integer serialNumber;
    private Category category;
    private String status;
}
