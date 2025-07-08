package com.farmerapp.specification;

import com.farmerapp.entity.Farmer;
import org.springframework.data.jpa.domain.Specification;

public class FarmerSpecification {

    public static Specification<Farmer> hasId(Long id) {
        return (root, query, cb) -> id == null ? null : cb.equal(root.get("id"), id);
    }

    public static Specification<Farmer> hasName(String name) {
        return (root, query, cb) ->
            name == null || name.trim().isEmpty() ? null :
            cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Farmer> hasStatus(Boolean status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }
}
