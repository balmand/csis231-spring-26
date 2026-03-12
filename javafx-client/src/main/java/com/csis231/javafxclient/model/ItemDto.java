package com.csis231.javafxclient.model;

public class ItemDto {
    private Long id;
    private String name;
    private Integer serialNumber;
    private String category;
    private String status;

    public ItemDto() {
    }

    public ItemDto(Long id, String name, Integer serialNumber, String category, String status) {
        this.id = id;
        this.name = name;
        this.serialNumber = serialNumber;
        this.category = category;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
