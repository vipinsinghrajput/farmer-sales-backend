package com.farmerapp.repository;

import com.farmerapp.entity.Farmer;
import com.farmerapp.entity.Product;
import com.farmerapp.payload.ProductDto;
import com.farmerapp.response.ApiResponse;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> ,JpaSpecificationExecutor<Product>{

Product save(ProductDto product);
Optional<Product> findByNameAndPriceAndFarmerId(String name, Double price, Long id);
List<Product> findByFarmerId(Long id);
Optional<Product> findByIdAndFarmerId(Long id, Long id2);
}
