package com.example.sockApi.repository;

import com.example.sockApi.entity.Sock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface SockRepository extends JpaRepository<Sock, Long>, JpaSpecificationExecutor<Sock> {

    Optional<Sock> findByColorAndCottonPercentage(String color, double cottonPercentage);

    List<Sock> findAllByColorAndCottonPercentage(String color, double cottonPercentage);

    List<Sock> findAllByColorAndCottonPercentageGreaterThan(String color, double cottonPercentage);

    List<Sock> findAllByColorAndCottonPercentageLessThan(String color, double cottonPercentage);

    List<Sock> findAllByCottonPercentageLessThan(double cottonPercentage);

    List<Sock> findAllByCottonPercentageGreaterThan(double cottonPercentage);

    List<Sock> findAllByCottonPercentageBetween(double minCottonPercentage, double maxCottonPercentage);

    List<Sock> findAllByCottonPercentage(double cottonPercentage);

    List<Sock> findAllByColor(String color);

    List<Sock> findAllByColorAndCottonPercentageBetween(String color, double minCottonPercentage, double maxCottonPercentage);

}
