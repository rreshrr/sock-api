package com.example.sockApi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "socks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "cotton_percentage", nullable = false)
    private double cottonPercentage;

    @Column(name = "count", nullable = false)
    private int count;
}
