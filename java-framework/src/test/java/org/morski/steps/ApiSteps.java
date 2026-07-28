package org.morski.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.morski.constants.HttpMethod;
import org.morski.dto.Account;
import org.morski.dto.api.CreateAccountRequest;
import org.morski.dto.api.DepositRequest;
import org.morski.dto.api.TransferRequest;
import org.morski.dto.api.WithdrawRequest;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;


public class ApiSteps {

    private final RequestSpecification spec;
    private final ObjectMapper objectMapper;

    private static final String ACCOUNTS_PATH = "/api/accounts";
    private static final String ACCOUNT_BY_ID_PATH = ACCOUNTS_PATH + "/{accountId}";
    private static final String DEPOSIT_PATH = ACCOUNT_BY_ID_PATH + "/deposit";
    private static final String WITHDRAW_PATH = ACCOUNT_BY_ID_PATH + "/withdraw";
    private static final String TRANSFER_PATH = ACCOUNT_BY_ID_PATH + "/transfer";

    public ApiSteps(RequestSpecification spec) {
        this.spec = spec;
        this.objectMapper = new ObjectMapper();
    }

    private Response executeRequest(String stepDescription, HttpMethod method, String path,
                                    Object requestBody, Object... pathParams) {
        return Allure.step(stepDescription, () -> {

            var request = given().spec(spec);

            if (requestBody != null) {
                String jsonBody = objectMapper.writeValueAsString(requestBody);
                Allure.addAttachment("Request body", "application/json", jsonBody);
                request.body(jsonBody);
            }

            var response = switch (method) {
                case GET -> request.get(path, pathParams);
                case POST -> request.post(path, pathParams);
            };

            var responseBody = response.asString();
            if (responseBody != null && !responseBody.isEmpty()) {
                Allure.addAttachment("Response body", "application/json", responseBody);
            }

            return response;
        });
    }

    public Response createAccount(Account account) {
        var request = CreateAccountRequest.builder()
                .customerId(account.getCustomerId())
                .initialBalance(account.getBalance())
                .currency(account.getCurrency().name())
                .build();

        return executeRequest("Create account", HttpMethod.POST, ACCOUNTS_PATH, request);
    }

    public Response getAccountById(UUID accountId) {
        return executeRequest(String.format("Get account %s", accountId),
                HttpMethod.GET, ACCOUNT_BY_ID_PATH, null, accountId);
    }


    public Response deposit(UUID accountId, BigDecimal amount) {
        var request = DepositRequest.builder().amount(amount).build();
        return executeRequest(String.format("Deposit %s to account %s", amount, accountId),
                HttpMethod.POST, DEPOSIT_PATH, request, accountId);
    }

    @Step("Withdraw funds: amount={amount}")
    public Response withdraw(String accountId, BigDecimal amount) {
        var request = WithdrawRequest.builder().amount(amount).build();
        return executeRequest(String.format("Withdraw funds: amount=%s", amount),
                HttpMethod.POST, WITHDRAW_PATH, request, accountId);
    }

    @Step("Transfer {amount}")
    public Response transfer(String senderId, String receiverId, BigDecimal amount) {
        var request = TransferRequest.builder().toAccountId(receiverId).amount(amount).build();
        return executeRequest(String.format("Transfer %s to user %s", amount, receiverId),
                HttpMethod.POST, TRANSFER_PATH, request, senderId);
    }
}
