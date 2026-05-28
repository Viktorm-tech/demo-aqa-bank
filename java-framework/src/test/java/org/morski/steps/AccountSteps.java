package org.morski.steps;

import org.morski.constants.AccountStatus;
import org.morski.constants.Currency;
import org.morski.dto.Account;

import java.math.BigDecimal;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.ThreadLocalRandom.current;

public class AccountSteps {

    public static Account createDefaultAccount() {
        return Account.builder()
                .id(randomUUID())
                .customerId(current().nextInt(1, Integer.MAX_VALUE))
                .balance(BigDecimal.valueOf(500, 2))
                .currency(Currency.USD)
                .status(AccountStatus.ACTIVE)
                .createdAt(now().minusDays(2))
                .updatedAt(now().minusDays(1))
                .build();
    }
}
