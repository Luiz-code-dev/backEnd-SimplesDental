package com.simplesdental.product.repository;

import com.simplesdental.product.model.ProductV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductV2Repository extends JpaRepository<ProductV2, Long> {
}
