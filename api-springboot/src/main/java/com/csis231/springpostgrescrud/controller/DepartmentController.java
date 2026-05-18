package com.csis231.springpostgrescrud.controller;

import com.csis231.springpostgrescrud.dto.DepartmentDto;
import com.csis231.springpostgrescrud.dto.PagedResponseDto;
import com.csis231.springpostgrescrud.exeption.BadRequestException;
import com.csis231.springpostgrescrud.service.DepartmentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/departments")
public class DepartmentController {
    private DepartmentService departmentService;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("name", "location");

    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody DepartmentDto departmentDto) {
        DepartmentDto savedDepartment = departmentService.createDepartment(departmentDto);
        return new ResponseEntity<>(savedDepartment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid department ID provided.");
        }
        DepartmentDto departmentDto = departmentService.getDepartmentById(id);
        return new ResponseEntity<>(departmentDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<DepartmentDto> departmentDtoList = departmentService.getAllDepartments();
        return new ResponseEntity<>(departmentDtoList, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponseDto<DepartmentDto>> searchDepartments(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        if (page < 0) {
            throw new BadRequestException("page must be >= 0");
        }
        if (size < 1 || size > 1000) {
            throw new BadRequestException("size must be between 1 and 100");
        }

        String normalizedSortField = sortField == null ? "name" : sortField.trim();
        if (!ALLOWED_SORT_FIELDS.contains(normalizedSortField)) {
            throw new BadRequestException("Invalid sortField. Allowed: " + String.join(", ", ALLOWED_SORT_FIELDS));
        }

        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDir);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("sortDir must be 'asc' or 'desc'");
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, normalizedSortField));
        Page<DepartmentDto> resultPage = departmentService.searchDepartments(q, pageable);

        PagedResponseDto<DepartmentDto> response = new PagedResponseDto<>(
                resultPage.getContent(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                normalizedSortField + "," + direction.name().toLowerCase()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDto> updateDepartment(@PathVariable Long id, @RequestBody DepartmentDto departmentDto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid department ID provided.");
        }
        DepartmentDto updatedDepartment = departmentService.updateDepartment(id, departmentDto);
        return new ResponseEntity<>(updatedDepartment, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid department ID provided.");
        }
        departmentService.deleteDepartment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
