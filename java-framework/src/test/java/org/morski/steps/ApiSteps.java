package org.morski.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.morski.dto.Account;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class ApiSteps {

    private final RequestSpecification spec;

    public ApiSteps(RequestSpecification spec) {
        this.spec = spec;
    }

    public Response createAccount(Account account) {
        return Allure.step(String.format("Create account: %s", account.toString()), () -> {

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

            Response response = given()
                    .spec(spec)
                    .body(body)
                    .when()
                    .post("/api/accounts");

            Allure.addAttachment("Response body", "application/json", response.asString());

            return response;
        });
    }

    @Step("Get account id {accountId}")
    public Response getAccountById(UUID accountId) {
        return given()
                .spec(spec)
                .when()
                .get(String.format("/api/accounts/%s", accountId));
    }

    @Step("Deposit funds: amount={amount}")
    public Response deposit(String accountID, BigDecimal amount) {

        String body = String.format("""
                {
                    "amount": %s
                }
                """,
                amount
        );

        Allure.addAttachment("Request body", "application/json", body);

        return given()
                .spec(spec)
                .body(body)
                .when()
                .post(String.format("/api/accounts/%s/deposit", accountID));
    }

    @Step("Withdraw funds: amount={amount}")
    public Response withdraw(String accountID, BigDecimal amount) {

        String body = String.format("""
                {
                    "amount": %s
                }
                """,
                amount
        );

        Allure.addAttachment("Request body", "application/json", body);

        return given()
                .spec(spec)
                .body(body)
                .when()
                .post(String.format("/api/accounts/%s/withdraw", accountID));
    }

    @Step("Transfer {amount}")
    public Response transfer(String senderId, String receiverId, BigDecimal amount) {

        String body = String.format("""
                {
                    "toAccountId": "%s",
                    "amount": %s
                }
                """,
                receiverId,
                amount
        );

        Allure.addAttachment("Request body", "application/json", body);

        return given()
                .spec(spec)
                .body(body)
                .when()
                .post(String.format("/api/accounts/%s/transfer", senderId));
    }
}
