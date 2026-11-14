package org.morski.steps;

import io.qameta.allure.Step;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.morski.dto.Account;
import org.morski.dto.api.CreateAccountRequest;
import org.morski.dto.api.DepositRequest;
import org.morski.dto.api.TransferRequest;
import org.morski.dto.api.WithdrawRequest;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.http.Method.GET;
import static io.restassured.http.Method.POST;


public class ApiSteps {

    private final RequestSpecification baseRequestSpec;
    private final RequestSpecification postRequestSpec;

    private static final String ACCOUNTS_PATH = "/api/accounts";
    private static final String ACCOUNT_BY_ID_PATH = ACCOUNTS_PATH + "/{accountId}";
    private static final String DEPOSIT_PATH = ACCOUNT_BY_ID_PATH + "/deposit";
    private static final String WITHDRAW_PATH = ACCOUNT_BY_ID_PATH + "/withdraw";
    private static final String TRANSFER_PATH = ACCOUNT_BY_ID_PATH + "/transfer";
    private static final String TRANSACTIONS_PATH = ACCOUNT_BY_ID_PATH + "/transactions";

    public ApiSteps(RequestSpecification baseRequestSpec, RequestSpecification postRequestSpec) {
        this.baseRequestSpec = baseRequestSpec;
        this.postRequestSpec = postRequestSpec;
    }

    private Response executeRequest(Method method,
                                    String path,
                                    Object requestBody,
                                    Map<String, ?> queryParams,
                                    Object... pathParams) {

        RequestSpecification request = given().spec(method == GET ? baseRequestSpec : postRequestSpec);

        if (queryParams != null && !queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }

        if (requestBody != null && method != GET) {
            request.body(requestBody);
        }

        return switch (method) {
            case GET -> request.get(path, pathParams);
            case POST -> request.post(path, pathParams);
            default -> throw new UnsupportedOperationException("HTTP method " + method + "not implemented");
        };
    }

    @Step("Create account")
    public Response createAccount(Account account) {
        var request = CreateAccountRequest.builder()
                .customerId(account.getCustomerId())
                .initialBalance(account.getBalance())
                .currency(account.getCurrency().name())
                .build();

        return executeRequest(POST, ACCOUNTS_PATH, request, Map.of());
    }

    @Step("Get account {accountId}")
    public Response getAccountById(UUID accountId) {
        return executeRequest(GET, ACCOUNT_BY_ID_PATH, null, Map.of(), accountId);
    }

    @Step("Deposit {amount} to account {accountId}")
    public Response deposit(UUID accountId, BigDecimal amount) {
        var request = DepositRequest.builder().amount(amount).build();
        return executeRequest(POST, DEPOSIT_PATH, request, Map.of(), accountId);
    }

    @Step("Withdraw funds: amount={amount}")
    public Response withdraw(String accountId, BigDecimal amount) {
        var request = WithdrawRequest.builder().amount(amount).build();
        return executeRequest(POST, WITHDRAW_PATH, request, Map.of(), accountId);
    }

    @Step("Transfer {amount} to user {receiverId}")
    public Response transfer(String senderId, String receiverId, BigDecimal amount) {
        var request = TransferRequest.builder().toAccountId(receiverId).amount(amount).build();
        return executeRequest(POST, TRANSFER_PATH, request, Map.of(), senderId);
    }

    @Step("Get transactions history for account {accountId}")
    public Response getTransactions(String accountId, int page, int size) {
        Map<String, Object> queryParams = Map.of(
                "page", page,
                "size", size
        );
        return executeRequest(GET, TRANSACTIONS_PATH, null, queryParams, accountId);
    }
}
