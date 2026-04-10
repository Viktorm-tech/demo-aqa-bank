package org.morski.api;

import org.morski.base.BaseApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Epic("API Tests")
@Story("Account operations")
public class AccountApiTests extends BaseApiTest {

    @Test
    @DisplayName("Get existing account")
    @Description("Positive: GET /account/{id} returns 200 and account data")
    public void getExistingAccountTest() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .when()
                .get("/account/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("balance", notNullValue())
                .body("currency", equalTo("USD"));
    }

    @Test
    @DisplayName("Get non-existent account")
    @Description("Negative: GET /account/{id} for non-existent id returns 404")
    public void getNonExistingAccountTest() {
        given()
                .spec(requestSpec)
                .pathParam("id", 9999)
                .when()
                .get("/account/{id}")
                .then()
                .statusCode(404)
                .body("error", containsString("not found"));
    }

    @Test
    @DisplayName("Create new account")
    @Description("Positive: POST /account creates account and returns 201")
    public void createAccountTest() {
        String requestBody = """
                {
                    "customerId": 10,
                    "initialBalance": 500,
                    "currency": "EUR"
                }
                """;

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/account")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("balance", equalTo(500))
                .body("currency", equalTo("EUR"));
    }
}
