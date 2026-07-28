package org.morski.dto.api;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CreateAccountRequest {
    private String customerId;
    private BigDecimal initialBalance;
    private String currency;
}
