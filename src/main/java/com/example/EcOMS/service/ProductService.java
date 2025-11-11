package com.example.EcOMS.service;



import com.example.EcOMS.dto.*;
import com.example.EcOMS.entity.Product;

import com.example.EcOMS.exceptionnn.InvalidOrderOperationException;
import com.example.EcOMS.exceptionnn.ProductNotFoundException;
import com.example.EcOMS.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public PageResponse<ProductDTO> getAllProducts(int page, int size, String sortBy, String sortDir) {

        if (page < 0) {
            throw new InvalidOrderOperationException("Page number cannot be negative");
        }
        if (size <= 0 || size > 100) {
            throw new InvalidOrderOperationException("Page size must be between 1 and 100");
        }

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPage = productRepository.findAll(pageable);

        return convertToPageResponse(productPage);
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return convertToDTO(product);
    }

    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {

        validateProductData(request.getName(), request.getPrice(), request.getStock());

        Product product = new Product();
        product.setName(request.getName().trim());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory().trim());
        product.setIsActive(true);
        product.setCreatedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));


        if (request.getName() != null && !request.getName().isBlank()) {
            validateProductName(request.getName());
            product.setName(request.getName().trim());
        }

        if (request.getPrice() != null) {
            validateProductPrice(request.getPrice());
            product.setPrice(request.getPrice());
        }

        if (request.getStock() != null) {
            validateProductStock(request.getStock());
            product.setStock(request.getStock());
        }

        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            product.setCategory(request.getCategory().trim());
        }

        if (request.getIsActive() != null) {
            if (!request.getIsActive() && product.getStock() > 0) {
                System.out.println("Warning: Deactivating product with stock: " + product.getName());
            }
            product.setIsActive(request.getIsActive());
        }

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        // Business rule: Stock bor bo'lgan mahsulotni o'chirib bo'lmaydi
        if (product.getStock() > 0) {
            throw new InvalidOrderOperationException(
                    "Cannot delete product with stock. Current stock: " + product.getStock());
        }

        product.setIsActive(false);
        productRepository.save(product);

    }

    public PageResponse<ProductDTO> searchProducts(String name, String category,
                                                   int page, int size,
                                                   String sortBy, String sortDir) {
        if (page < 0) {
            throw new InvalidOrderOperationException("Page number cannot be negative");
        }
        if (size <= 0 || size > 100) {
            throw new InvalidOrderOperationException("Page size must be between 1 and 100");
        }

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        String searchName = (name != null && !name.isBlank()) ? name.trim() : null;
        String searchCategory = (category != null && !category.isBlank()) ? category.trim() : null;

        Page<Product> productPage = productRepository.searchProducts(
                searchName, searchCategory, pageable);

        return convertToPageResponse(productPage);
    }

    private void validateProductData(String name, java.math.BigDecimal price, Integer stock) {
        validateProductName(name);
        validateProductPrice(price);
        validateProductStock(stock);
    }

    private void validateProductName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidOrderOperationException("Product name cannot be empty");
        }
        if (name.trim().length() < 2) {
            throw new InvalidOrderOperationException("Product name must be at least 2 characters");
        }
    }

    private void validateProductPrice(java.math.BigDecimal price) {
        if (price == null) {
            throw new InvalidOrderOperationException("Product price cannot be null");
        }
        if (price.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new InvalidOrderOperationException("Product price must be greater than 0");
        }
    }

    private void validateProductStock(Integer stock) {
        if (stock == null) {
            throw new InvalidOrderOperationException("Product stock cannot be null");
        }
        if (stock < 0) {
            throw new InvalidOrderOperationException("Product stock cannot be negative");
        }
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCategory(product.getCategory());
        dto.setIsActive(product.getIsActive());
        dto.setCreatedAt(product.getCreatedAt());
        return dto;
    }

    private PageResponse<ProductDTO> convertToPageResponse(Page<Product> productPage) {
        List<ProductDTO> content = productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        PageResponse<ProductDTO> response = new PageResponse<>();
        response.setContent(content);
        response.setPageNumber(productPage.getNumber());
        response.setPageSize(productPage.getSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setLast(productPage.isLast());

        return response;
    }
}