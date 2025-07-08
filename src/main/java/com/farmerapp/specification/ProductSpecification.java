package com.farmerapp.specification;

import com.farmerapp.entity.Product;
import com.farmerapp.entity.ProductCategory;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> hasNameLike(String name) {
        return (root, query, cb) ->
                name == null || name.isBlank() ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> belongsToFarmer(Long farmerId) {
        return (root, query, cb) -> farmerId == null ? null : cb.equal(root.get("farmer").get("id"), farmerId);
    }
    
    public static Specification<Product> hasPriceBetween(Double minPrice, Double maxPrice) {
        return (root, query, cb) -> {
            if (minPrice != null && maxPrice != null) {
                return cb.between(root.get("price"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            } else if (maxPrice != null) {
                return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            } else {
                return null;
            }
        };
    }

    public static Specification<Product> hasCategory(ProductCategory category) {
        return (root, query, cb) -> category == null ? null : cb.equal(root.get("category"), category);
    }

    public static Specification<Product> isAvailable(boolean available) {
        return (root, query, cb) -> cb.equal(root.get("available"), available);
    }
}
