package org.morski.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;

public class ApiSteps {

    private final RequestSpecification spec;

    public ApiSteps(RequestSpecification spec) {
        this.spec = spec;
    }

    @Step("Create account: customerId = {customerId}, balance = {initialBalance}, currency = {currency}")
    public Response createAccount(String customerId, BigDecimal initialBalance, String currency) {

        String body = String.format("""
                {
                    "customerId": %s,
                    "initialBalance": %s,
                    "currency": "%s"
                }
                """, customerId, initialBalance, currency);

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
