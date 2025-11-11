package com.example.EcOMS.repository;



import com.example.EcOMS.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product")
    List<Order> findAll();


    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    Optional<Order> findById(Long id);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.customerEmail = :email")
    List<Order> findByCustomerEmail(String email);
}
