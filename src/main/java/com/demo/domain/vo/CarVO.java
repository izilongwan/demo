package com.demo.domain.vo;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.demo.domain.entity.Order;
import com.demo.enumeration.Color;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarVO implements Serializable {
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
