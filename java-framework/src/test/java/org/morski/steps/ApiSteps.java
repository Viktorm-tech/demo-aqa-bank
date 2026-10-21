package org.morski.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.morski.dto.Account;
import org.morski.dto.api.CreateAccountRequest;
import org.morski.dto.api.DepositRequest;
import org.morski.dto.api.TransferRequest;
import org.morski.dto.api.WithdrawRequest;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Function;

import static io.restassured.RestAssured.given;


public class ApiSteps {

    private final RequestSpecification spec;
    private final ObjectMapper objectMapper;

    private static final String ACCOUNTS_PATH = "/api/accounts";
    private static final String ACCOUNT_BY_ID_PATH = ACCOUNTS_PATH + "/{%s}";
    private static final String DEPOSIT_PATH = ACCOUNT_BY_ID_PATH + "/deposit";
    private static final String WITHDRAW_PATH = ACCOUNT_BY_ID_PATH + "/withdraw";
    private static final String TRANSFER_PATH = ACCOUNT_BY_ID_PATH + "/transfer";
    private static final String TRANSACTIONS_PATH = ACCOUNT_BY_ID_PATH + "/transactions";

    public ApiSteps(RequestSpecification spec) {
        this.spec = spec;
        this.objectMapper = new ObjectMapper();
    }

    private Response apiCall(Function<RequestSpecification, Response> call) {
        var prepared = given().spec(spec);
        var response = call.apply(prepared);
        attachResponse(response);
        return response;
    }

    private Response apiCallJson(Object body,
                                Function<RequestSpecification, Response> call) {
        var prepared = given().spec(spec);
        if (body != null) {
            String json;
            try {
                json = objectMapper.writeValueAsString(body);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize request body", e);
            }
            Allure.addAttachment("Request body", "application/json", json);
            prepared.body(json);
        }
        var response = call.apply(prepared);
        attachResponse(response);
        return response;
    }

    private void attachResponse(Response response) {
        var body = response.asString();
        if (body == null || body.isEmpty()) return;
        Allure.addAttachment("Response body", "application/json", body);
    }

    @Step("Create account")
    public Response createAccount(Account account) {
        var request = CreateAccountRequest.builder()
                .customerId(account.getCustomerId())
                .initialBalance(account.getBalance())
                .currency(account.getCurrency().name())
                .build();

        return apiCallJson(request, r -> r.post(ACCOUNTS_PATH));
    }

    @Step("Get account {accountId}")
    public Response getAccountById(UUID accountId) {
        return apiCall(r -> r.get(ACCOUNT_BY_ID_PATH, accountId));
    }

    @Step("Deposit {amount} to account {accountId}")
    public Response deposit(UUID accountId, BigDecimal amount) {
        var request = DepositRequest.builder().amount(amount).build();
        return apiCallJson(request, r -> r.post(DEPOSIT_PATH, accountId));
    }

    @Step("Withdraw funds: amount={amount}")
    public Response withdraw(String accountId, BigDecimal amount) {
        var request = WithdrawRequest.builder().amount(amount).build();
        return apiCallJson(request, r -> r.post(WITHDRAW_PATH, accountId));
    }

    @Step("Transfer {amount} to user {receiverId}")
    public Response transfer(String senderId, String receiverId, BigDecimal amount) {
        var request = TransferRequest.builder().toAccountId(receiverId).amount(amount).build();
        return apiCallJson(request, r -> r.post(TRANSFER_PATH, senderId));
    }

    @Step("Get transactions history for account {accountId}")
    public Response getTransactions(String accountId, int page, int size) {
        return apiCall(r -> r
                .queryParam("page", page)
                .queryParam("size", size)
                .get(TRANSACTIONS_PATH, accountId));
    }
}
