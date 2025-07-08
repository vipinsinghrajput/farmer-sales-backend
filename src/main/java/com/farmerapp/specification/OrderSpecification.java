package com.farmerapp.specification;

import com.farmerapp.entity.Farmer;
import com.farmerapp.entity.OrderEntity;
import com.farmerapp.entity.OrderItem;
import com.farmerapp.entity.OrderStatus;
import com.farmerapp.entity.Product;

import jakarta.persistence.criteria.Join;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class OrderSpecification {

	  public static Specification<OrderEntity> hasStatus(OrderStatus status) {
	        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
	    }

	    public static Specification<OrderEntity> hasOrderDateRange(LocalDate fromDate, LocalDate toDate) {
	        return (root, query, cb) -> {
	            if (fromDate == null && toDate == null) return null;

	            if (fromDate != null && toDate != null) {
	                return cb.between(
	                    root.get("orderDate"),
	                    fromDate.atStartOfDay(),
	                    toDate.plusDays(1).atStartOfDay()
	                );
	            } else if (fromDate != null) {
	                // From date to max
	                return cb.greaterThanOrEqualTo(root.get("orderDate"), fromDate.atStartOfDay());
	            } else {
	                // To date only (less than or equal)
	                return cb.lessThanOrEqualTo(root.get("orderDate"), toDate.plusDays(1).atStartOfDay());
	            }
	        };
	    }

	    public static Specification<OrderEntity> belongsTofarmer(Long farmerId) {
	    	
	    	  return (root, query, cb) -> {
	    	        Join<OrderEntity, OrderItem> orderItems = root.join("orderItems");
	    	        Join<OrderItem, Product> product = orderItems.join("product");
	    	        Join<Product, Farmer> farmer = product.join("farmer");

	    	        return cb.equal(farmer.get("id"), farmerId);
	    	    };
	    	
//	        return (root, query, cb) -> cb.equal(root.get("farmer").get("id"), farmerId);
	    }
	    public static Specification<OrderEntity> belongsToConsumer(Long consumerId) {
	        return (root, query, cb) -> cb.equal(root.get("consumer").get("id"), consumerId);
	    }
}
