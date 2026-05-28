package org.morski.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.morski.dto.Account;

import static io.restassured.RestAssured.given;

public class ApiSteps {

    private final RequestSpecification spec;

    public ApiSteps(RequestSpecification spec) {
        this.spec = spec;
    }

    @Step("Create account")
    public Response createAccount(Account account) {

        String body = String.format("""
                {
                    "customerId": %s,
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
                .extract()
                .response();
    }
}
