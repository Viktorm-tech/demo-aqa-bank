package org.morski.validators;

import io.qameta.allure.Step;
import org.morski.constants.AccountStatus;
import org.morski.dto.Account;
import org.morski.dto.AccountResponse;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ApiValidators {

    @Step("Validate created Account response")
    public static void validateNewAccountResponse(AccountResponse accountResponse, Account expectedAccount) {

        assertAll(
                () -> assertNotNull(accountResponse.getId()),
                () -> assertEquals(expectedAccount.getCustomerId(), accountResponse.getCustomerId()),
                () -> assertEquals(expectedAccount.getBalance(), accountResponse.getBalance()),
                () -> assertEquals(expectedAccount.getCurrency(), accountResponse.getCurrency()),
                () -> assertEquals(AccountStatus.ACTIVE, accountResponse.getStatus())
        );
    }

    @Step("Validate Account response")
    public static void validateAccountResponse(AccountResponse accountResponse, Account expectedAccount) {

        assertAll(
                () -> assertEquals(expectedAccount.getId(), accountResponse.getId()),
                () -> assertEquals(expectedAccount.getCustomerId(), accountResponse.getCustomerId()),
                () -> assertEquals(expectedAccount.getBalance(), accountResponse.getBalance()),
                () -> assertEquals(expectedAccount.getCurrency(), accountResponse.getCurrency()),
                () -> assertEquals(expectedAccount.getStatus(), accountResponse.getStatus())
        );
    }
}
