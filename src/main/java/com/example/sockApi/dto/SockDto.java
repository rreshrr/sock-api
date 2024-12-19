package com.example.sockApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SockDto {

    private Long id;

    private String color;

    private double cottonPercentage;

    private int count;
}
