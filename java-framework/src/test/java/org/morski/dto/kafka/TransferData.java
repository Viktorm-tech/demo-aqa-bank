package org.morski.dto.kafka;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TransferData {
    private BigDecimal amount;
    private String toAccount;
}
