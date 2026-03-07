package com.csis231.springpostgrescrud.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)

    private Integer serialNumber;

    @PrePersist
    public void generateSerialNumber() {
        if (serialNumber == null) {
            serialNumber = (int)(Math.random() * 900000) + 100000;
        }
    }

    private String category;

    private String status;
}
