package com.example.EcOMS.repository;



import com.example.EcOMS.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:category IS NULL OR LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%')))")
    Page<Product> searchProducts(
            @Param("name") String name,
            @Param("category") String category,
            Pageable pageable);
}
