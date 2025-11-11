package com.example.EcOMS.entity;

import com.example.EcOMS.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="customer_name",nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerPhone;

    @Column(name="address",nullable = false)
    private String customerEmail;

    @Column(name = "create_at")
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<OrderItem> orderItems=new ArrayList<>();

    @PrePersist
    public void onCreate() {
        orderDate=LocalDateTime.now();
        if (orderStatus==null) {
            orderStatus=OrderStatus.PENDING;
        }
    }

    @PreUpdate
    public void onUpdate() {
        orderDate=LocalDateTime.now();
    }
}
