package com.demo.domain.vo;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldNameConstants
@Valid
public class AuthortityTokenVO {
    private String accessToken;
    @NotBlank
    private String refreshToken;
    private List<String> authorities;
}
