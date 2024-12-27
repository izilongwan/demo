package com.demo.pojo.vo;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.demo.enumeration.Color;
import com.demo.pojo.entity.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarVO {
    Integer id;
    @NotBlank
    String name;
    @NotEmpty
    String brand;
    String country;

    @Valid
    @NotNull
    Order order;

    @Valid
    @NotNull
    Color color;
}
