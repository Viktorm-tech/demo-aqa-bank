package org.morski.steps;

import org.apache.commons.lang3.RandomStringUtils;
import org.morski.constants.AccountStatus;
import org.morski.constants.Currency;
import org.morski.dto.Account;

import java.math.BigDecimal;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;

public class AccountSteps {

    public static Account createDefaultAccount() {
        return Account.builder()
                .id(randomUUID())
                .customerId(RandomStringUtils.insecure().nextAlphanumeric(10))
                .balance(BigDecimal.valueOf(500, 2))
                .currency(Currency.USD)
                .status(AccountStatus.ACTIVE)
                .createdAt(now().minusDays(2))
                .updatedAt(now().minusDays(1))
                .build();
    }

    public static Account createAccountWithBalance(BigDecimal balance) {
        return Account.builder()
                .id(randomUUID())
                .customerId(RandomStringUtils.insecure().nextAlphanumeric(10))
                .balance(balance)
                .currency(Currency.USD)
                .status(AccountStatus.ACTIVE)
                .createdAt(now().minusDays(2))
                .updatedAt(now().minusDays(1))
                .build();
    }
}
