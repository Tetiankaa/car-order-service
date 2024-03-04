package com.example.orderservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDTO {
    private Integer id;

    private Long timestamp;

    private String brand;

    private Double price;

    private Long year;

}
