package com.csis231.javafxclient.service;

import com.csis231.javafxclient.model.DepartmentDto;
import com.csis231.javafxclient.model.EmployeeDto;
<<<<<<< HEAD
import com.csis231.javafxclient.model.ItemDto;
=======
import com.csis231.javafxclient.model.UserDto;
>>>>>>> upstream/main
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.csis231.javafxclient.model.OrderDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/v1";
    private final HttpClient httpClient;
    private final Gson gson;
    private String authToken;

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    // User endpoints
    public UserDto registerUser(UserDto user) throws IOException, InterruptedException {
        String json = gson.toJson(user);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            return gson.fromJson(response.body(), UserDto.class);
        }

        throw new RuntimeException("Failed to register user: " + response.statusCode() + " - " + response.body());
    }

    // Employee endpoints
    public List<EmployeeDto> getAllEmployees() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/employees"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<EmployeeDto>>() {}.getType());
        }
        throw new RuntimeException("Failed to fetch employees: " + response.statusCode());
    }

    public EmployeeDto getEmployeeById(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/employees/" + id))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), EmployeeDto.class);
        }
        throw new RuntimeException("Failed to fetch employee: " + response.statusCode());
    }

    public EmployeeDto createEmployee(EmployeeDto employee) throws IOException, InterruptedException {
        String json = gson.toJson(employee);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/employees"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
            return gson.fromJson(response.body(), EmployeeDto.class);
        }
        throw new RuntimeException("Failed to create employee: " + response.statusCode() + " - " + response.body());
    }

    public EmployeeDto updateEmployee(Long id, EmployeeDto employee) throws IOException, InterruptedException {
        String json = gson.toJson(employee);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/employees/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), EmployeeDto.class);
        }
        throw new RuntimeException("Failed to update employee: " + response.statusCode());
    }

    // Department endpoints
    public List<DepartmentDto> getAllDepartments() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/departments"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<DepartmentDto>>() {}.getType());
        }
        throw new RuntimeException("Failed to fetch departments: " + response.statusCode());
    }

    public DepartmentDto getDepartmentById(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/departments/" + id))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), DepartmentDto.class);
        }
        throw new RuntimeException("Failed to fetch department: " + response.statusCode());
    }

    public DepartmentDto createDepartment(DepartmentDto department) throws IOException, InterruptedException {
        String json = gson.toJson(department);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/departments"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
            return gson.fromJson(response.body(), DepartmentDto.class);
        }
        throw new RuntimeException("Failed to create department: " + response.statusCode() + " - " + response.body());
    }

    public DepartmentDto updateDepartment(Long id, DepartmentDto department) throws IOException, InterruptedException {
        String json = gson.toJson(department);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/departments/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), DepartmentDto.class);
        }
        throw new RuntimeException("Failed to update department: " + response.statusCode());
    }

    public void deleteDepartment(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/departments/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204) {
            throw new RuntimeException("Failed to delete department: " + response.statusCode());
        }
    }
<<<<<<< HEAD

    // Item endpoints
    public List<ItemDto> getAllItems() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/items"))
=======
    public List<OrderDto> getAllOrders() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/orders"))
>>>>>>> upstream/main
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
<<<<<<< HEAD
            return gson.fromJson(response.body(), new TypeToken<List<ItemDto>>(){}.getType());
        }
        throw new RuntimeException("Failed to fetch items: " + response.statusCode());
    }

    public ItemDto getItemById(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/items/" + id))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), ItemDto.class);
        }
        throw new RuntimeException("Failed to fetch item: " + response.statusCode());
    }

    public ItemDto createItem(ItemDto item) throws IOException, InterruptedException {
        String json = gson.toJson(item);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/items"))
=======
            return gson.fromJson(response.body(), new TypeToken<List<OrderDto>>() {}.getType());
        }
        throw new RuntimeException("Failed to fetch orders: " + response.statusCode());
    }

    public OrderDto createOrder(OrderDto order) throws IOException, InterruptedException {
        String json = gson.toJson(order);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/orders"))
>>>>>>> upstream/main
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
<<<<<<< HEAD
            return gson.fromJson(response.body(), ItemDto.class);
        }
        throw new RuntimeException("Failed to create item: " + response.statusCode() + " - " + response.body());
    }

    public ItemDto updateItem(Long id, ItemDto item) throws IOException, InterruptedException {
        String json = gson.toJson(item);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/items/" + id))
=======
            return gson.fromJson(response.body(), OrderDto.class);
        }
        throw new RuntimeException("Failed to create order: " + response.statusCode() + " - " + response.body());
    }

    public OrderDto updateOrder(Long id, OrderDto order) throws IOException, InterruptedException {
        String json = gson.toJson(order);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/orders/" + id))
>>>>>>> upstream/main
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
<<<<<<< HEAD
            return gson.fromJson(response.body(), ItemDto.class);
        }
        throw new RuntimeException("Failed to update item: " + response.statusCode());
    }

    public void deleteItem(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/items/" + id))
=======
            return gson.fromJson(response.body(), OrderDto.class);
        }
        throw new RuntimeException("Failed to update order: " + response.statusCode() + " - " + response.body());
    }

    public void deleteOrder(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/orders/" + id))
>>>>>>> upstream/main
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
<<<<<<< HEAD
        if (response.statusCode() != 204) {
            throw new RuntimeException("Failed to delete item: " + response.statusCode());
=======
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to delete order: " + response.statusCode() + " - " + response.body());
>>>>>>> upstream/main
        }
    }
}
