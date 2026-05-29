package org.morski.dto;

import lombok.Builder;
import lombok.Getter;
import org.morski.constants.AccountStatus;
import org.morski.constants.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Account {

    private final UUID id;
    private final String customerId;
    private final BigDecimal balance;
    private final Currency currency;
    private final AccountStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
