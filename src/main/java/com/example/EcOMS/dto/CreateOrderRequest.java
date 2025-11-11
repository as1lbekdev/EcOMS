package com.example.EcOMS.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;

    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String customerName;

    @NotBlank(message = "Phone number is required")
    @Size(min = 9, max = 20, message = "Phone number must be between 9 and 20 characters")
    private String customerPhone;

    @NotBlank(message = "Delivery address is required")
    @Size(min = 10, max = 500, message = "Address must be between 10 and 500 characters")
    private String deliveryAddress;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;
}