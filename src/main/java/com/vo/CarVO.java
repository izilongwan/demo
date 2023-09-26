package com.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CarVO {
    Integer id;
    String name;
    String brand;
    String country;
}
