package com.demo.domain.entity;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.demo.enumeration.Color;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @NotEmpty
    String name;

    @Max(value = 100)
    @Min(value = 20)
    @NotNull
    Integer age;

    @Valid
    @NotNull
    Order order;

    @Valid
    @NotNull
    Color color;
}
