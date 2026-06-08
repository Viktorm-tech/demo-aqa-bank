package org.morski.validators;

import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;
import org.morski.constants.AccountStatus;
import org.morski.dto.Account;
import org.morski.dto.AccountResponse;

public class ApiValidators {

    @Step("Validate created Account response")
    public static void validateNewAccountResponse(AccountResponse accountResponse, Account expectedAccount) {

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(accountResponse.getId()).as("id").isNotNull();
            softly.assertThat(accountResponse.getCustomerId())
                    .as("customer_id")
                    .isEqualTo(expectedAccount.getCustomerId());
            softly.assertThat(accountResponse.getBalance())
                    .as("balance")
                    .isEqualTo(expectedAccount.getBalance());
            softly.assertThat(accountResponse.getCurrency())
                    .as("currency")
                    .isEqualTo(expectedAccount.getCurrency());
            softly.assertThat(accountResponse.getStatus()).as("status").isEqualTo(AccountStatus.ACTIVE);
        });
    }

    @Step("Validate Account response")
    public static void validateAccountResponse(AccountResponse accountResponse, Account expectedAccount) {

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(accountResponse.getId()).as("id").isEqualTo(expectedAccount.getId());
            softly.assertThat(accountResponse.getCustomerId())
                    .as("customer_id")
                    .isEqualTo(expectedAccount.getCustomerId());
            softly.assertThat(accountResponse.getBalance())
                    .as("balance")
                    .isEqualTo(expectedAccount.getBalance());
            softly.assertThat(accountResponse.getCurrency())
                    .as("currency")
                    .isEqualTo(expectedAccount.getCurrency());
            softly.assertThat(accountResponse.getStatus())
                    .as("status")
                    .isEqualTo(expectedAccount.getStatus());
        });
    }
}
