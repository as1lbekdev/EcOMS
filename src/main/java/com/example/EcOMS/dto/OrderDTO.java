package com.example.EcOMS.dto;

import com.example.EcOMS.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;
    private String customerEmail;
    private  String customerName;
    private String customerPhone;
    private String customerAddress;
    private BigDecimal totalPrice;
    private OrderStatus orderStatus;
    private List<OrderItemDTO> orderItems;
    private LocalDateTime orderDate;

}
