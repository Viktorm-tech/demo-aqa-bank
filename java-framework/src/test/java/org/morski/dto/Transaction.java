package org.morski.dto;

import lombok.Builder;
import lombok.Getter;
import org.morski.constants.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Transaction {

    private final UUID id;
    private final UUID accountId;
    private final BigDecimal amount;
    private final TransactionType type;
    private final UUID relatedAccountId;
    private final String description;
    private final LocalDateTime timestamp;
}

