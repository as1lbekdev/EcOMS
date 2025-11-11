package com.example.EcOMS.dto;



import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    @Size(min = 2, max = 255, message = "Product name must be between 2 and 255 characters")
    private String name;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price format is invalid")
    private BigDecimal price;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @Size(min = 2, max = 100, message = "Category must be between 2 and 100 characters")
    private String category;

    private Boolean isActive;
}