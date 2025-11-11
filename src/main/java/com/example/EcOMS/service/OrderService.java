package com.example.EcOMS.service;


import com.example.EcOMS.dto.*;
import com.example.EcOMS.entity.Order;
import com.example.EcOMS.entity.OrderItem;

import com.example.EcOMS.entity.Product;

import com.example.EcOMS.enums.OrderStatus;
import com.example.EcOMS.exceptionnn.*;
import com.example.EcOMS.repository.OrderRepository;
import com.example.EcOMS.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
        return convertToDTO(order);
    }

    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        validateNoDuplicateProducts(request.getItems());

        Order order = new Order();
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setOrderStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(
                            "Product not found with id: " + itemRequest.getProductId()));

            if (product.getIsActive() == null || !product.getIsActive()) {
                throw new InvalidOrderOperationException(
                        "Product is not available: " + product.getName());
            }

            if (product.getStock() <= 0) {
                throw new InsufficientStockException(
                        "Product is out of stock: " + product.getName());
            }

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                        String.format("Insufficient stock for product: %s. Available: %d, Requested: %d",
                                product.getName(), product.getStock(), itemRequest.getQuantity()));
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPrice());

            BigDecimal subtotal = product.getPrice()
                    .multiply(new BigDecimal(itemRequest.getQuantity()));
            orderItem.setTotalPrice(subtotal);
            totalAmount = totalAmount.add(subtotal);

            order.getOrderItems().add(orderItem);

            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);
        }

        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOrderOperationException("Order total amount must be greater than zero");
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderOperationException(
                    "Only orders with PENDING status can be updated. Current status: " + order.getOrderStatus());
        }

        if (request.getStatus() == OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        order.setOrderStatus(request.getStatus());
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        if (order.getOrderStatus() != OrderStatus.PENDING && order.getOrderStatus() != OrderStatus.CANCELLED) {
            throw new InvalidOrderOperationException(
                    "Only PENDING or CANCELLED orders can be deleted. Current status: " + order.getOrderStatus());
        }

        if (order.getOrderStatus() == OrderStatus.PENDING) {
            restoreStock(order);
        }

        orderRepository.delete(order);
    }

    public List<OrderDTO> getOrdersByCustomerEmail(String email) {
        List<Order> orders = orderRepository.findByCustomerEmail(email);
        if (orders.isEmpty()) {
            throw new OrderNotFoundException("No orders found for email: " + email);
        }
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    private void validateNoDuplicateProducts(List<OrderItemRequest> items) {
        Set<Long> productIds = new HashSet<>();
        for (OrderItemRequest item : items) {
            if (!productIds.add(item.getProductId())) {
                throw new DuplicateProductInOrderException(
                        "Duplicate product found in order. Product ID: " + item.getProductId());
            }
        }
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerPhone(order.getCustomerPhone());

        dto.setTotalPrice(order.getTotalAmount());
        dto.setOrderStatus(order.getOrderStatus());


        List<OrderItemDTO> items = order.getOrderItems().stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());
        dto.setOrderItems(items);

        return dto;
    }

    private OrderItemDTO convertItemToDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }
}