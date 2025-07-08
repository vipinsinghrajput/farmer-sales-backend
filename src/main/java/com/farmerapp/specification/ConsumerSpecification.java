package com.farmerapp.specification;

import com.farmerapp.entity.Consumer;
import org.springframework.data.jpa.domain.Specification;

public class ConsumerSpecification {

    public static Specification<Consumer> hasId(Long id) {
        return (root, query, cb) -> id == null ? null : cb.equal(root.get("id"), id);
    }

    public static Specification<Consumer> hasName(String name) {
        return (root, query, cb) -> (name == null || name.isBlank()) ? null :
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Consumer> hasStatus(Boolean status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }
}

