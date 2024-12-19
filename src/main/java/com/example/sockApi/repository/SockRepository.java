package com.example.sockApi.repository;

import com.example.sockApi.entity.Sock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SockRepository extends JpaRepository<Sock, Long>, JpaSpecificationExecutor<Sock> {

    Optional<Sock> findByColorAndCottonPercentage(String color, double cottonPercentage);

}
