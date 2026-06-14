package com.admin.system.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ValuationPriceDto {
    private Long id;
    private String city;
    private String district;
    private String address;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BigDecimal area;
    private LocalDate valuationTime;
}
