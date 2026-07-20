package org.morski.tests;

import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.morski.base.BaseTest;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.morski.steps.AccountSteps.createAccountWithBalance;

public class TransferTest extends BaseTest {
    @Test
    @DisplayName("Transfer between accounts")
    @Description("Positive: POST /api/accounts/{fromId}/transfer returns 200 OK amount transferred between accounts")
    public void transferTest() {

        var senderBalance = BigDecimal.valueOf(500, 2);
        var sender = createAccountWithBalance(senderBalance);
        databaseSteps.createAccount(sender);
        var senderId = sender.getId().toString();

        var receiverBalance = BigDecimal.valueOf(200, 2);
        var receiver = createAccountWithBalance(receiverBalance);
        databaseSteps.createAccount(receiver);
        var receiverId = receiver.getId().toString();

        var transferAmount = BigDecimal.valueOf(400, 2);
        var response = apiSteps.transfer(senderId, receiverId, transferAmount);
        response.then().statusCode(200);

        var headerDate = response.getHeader("Date");
        var updatedAt = ZonedDateTime.parse(headerDate, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();

        var expectedSenderBalance = sender.getBalance().subtract(transferAmount);
        databaseSteps.verifyBalanceChanged(sender, expectedSenderBalance, updatedAt);

        var expectedReceiverBalance = receiver.getBalance().add(transferAmount);
        databaseSteps.verifyBalanceChanged(receiver, expectedReceiverBalance, updatedAt);

        kafkaSteps.verifyTransferCompletedEvent(senderId, receiverId, transferAmount, 20);
    }
}
