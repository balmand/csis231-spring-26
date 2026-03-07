package com.csis231.springpostgrescrud.repository;

import com.csis231.springpostgrescrud.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
