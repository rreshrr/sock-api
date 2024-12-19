package com.example.sockApi.repository.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationBuilder<T> {

    public Specification<T> build(List<Specification<T>> specifications) {
        Specification<T> result = Specification.where(null);
        for (Specification<T> spec : specifications) {
            result = result.and(spec);
        }
        return result;
    }
}