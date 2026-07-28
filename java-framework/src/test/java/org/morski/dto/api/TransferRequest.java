package org.morski.dto.api;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferRequest {
    private String toAccountId;
    private BigDecimal amount;
}
