package com.ecommerce.catalog_service.Controller;

import com.ecommerce.catalog_service.Dto.CategoryDto;
import com.ecommerce.catalog_service.Dto.CreateCategoryRequest;
import com.ecommerce.catalog_service.Dto.DtoMapper;
import com.ecommerce.catalog_service.Service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CatalogService catalogService;

    public CategoryController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public Flux<CategoryDto> getAll() {
        return catalogService.getAllActiveCategories().map(DtoMapper::toDto);
    }

    @GetMapping("/{id}")
    public Mono<CategoryDto> getById(@PathVariable String id) {
        return catalogService.getCategoryById(id).map(DtoMapper::toDto);
    }

    @GetMapping("/slug/{slug}")
    public Mono<CategoryDto> getBySlug(@PathVariable String slug) {
        return catalogService.getCategoryBySlug(slug).map(DtoMapper::toDto);
    }

    @GetMapping("/{id}/children")
    public Flux<CategoryDto> getChildren(@PathVariable String id) {
        return catalogService.getCategoryChildren(id).map(DtoMapper::toDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CategoryDto> create(@Valid @RequestBody CreateCategoryRequest request) {
        return catalogService.createCategory(DtoMapper.toEntity(request)).map(DtoMapper::toDto);
    }
}