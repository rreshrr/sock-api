package com.example.sockApi.repository.specifications;

import com.example.sockApi.entity.Sock;
import org.springframework.data.jpa.domain.Specification;

public class SockSpecifications {

    public static Specification<Sock> colorEquals(String color) {
        return (root, query, criteriaBuilder) ->
                color == null ? null : criteriaBuilder.equal(root.get("color"), color);
    }

    public static Specification<Sock> cottonPercentageEquals(Double exactCottonPercentage) {
        return (root, query, criteriaBuilder) ->
                exactCottonPercentage == null ? null : criteriaBuilder.equal(root.get("cottonPercentage"), exactCottonPercentage);
    }

    public static Specification<Sock> cottonPercentageBetween(Double minCottonPercentage, Double maxCottonPercentage) {
        return (root, query, criteriaBuilder) -> {
            if (minCottonPercentage == null && maxCottonPercentage == null) {
                return null;
            } else if (minCottonPercentage != null && maxCottonPercentage != null) {
                return criteriaBuilder.between(root.get("cottonPercentage"), minCottonPercentage, maxCottonPercentage);
            } else if (minCottonPercentage != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("cottonPercentage"), minCottonPercentage);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("cottonPercentage"), maxCottonPercentage);
            }
        };
    }
}