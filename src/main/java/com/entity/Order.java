package com.entity;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Data;

@Data
public class Order {
    @NotBlank
    String id;

    String name;

    @DecimalMin("1")
    @NotNull
    Double price;

    @NotNull
    @Positive
    Integer count;
}
