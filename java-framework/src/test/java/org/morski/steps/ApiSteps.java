package org.morski.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.specification.RequestSpecification;
import org.morski.dto.Account;
import org.morski.dto.AccountResponse;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class ApiSteps {

    private final RequestSpecification spec;

    public ApiSteps(RequestSpecification spec) {
        this.spec = spec;
    }

    @Step("Create account")
    public AccountResponse createAccount(Account account) {

        String body = String.format("""
                {
                    "customerId": "%s",
                    "initialBalance": %s,
                    "currency": "%s"
                }
                """,
                account.getCustomerId(),
                account.getBalance(),
                account.getCurrency()
        );

        Allure.addAttachment("Request body", "application/json", body);

        return given()
                .spec(spec)
                .body(body)
                .when()
                .post("/api/accounts")
                .then()
                .statusCode(201)
                .extract().as(AccountResponse.class);
    }

    @Step("Get account id {accountId}")
    public AccountResponse getAccountById(UUID accountId) {
        return given()
                .spec(spec)
                .when()
                .get(String.format("/api/accounts/%s", accountId))
                .then()
                .statusCode(200)
                .extract()
                .as(AccountResponse.class);
    }
}
