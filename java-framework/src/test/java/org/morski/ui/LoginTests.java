package org.morski.ui;

import com.codeborne.selenide.Condition;
import org.morski.ui.pages.LoginPage;
import org.morski.ui.pages.AccountsOverviewPage;
import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

public class LoginTests {

    @Test
    @DisplayName("Successfully log in")
    @Description("Positive: login and password are valid - welcome page is opened")
    public void successLoginTest() {
        LoginPage loginPage = open("https://parabank.parasoft.com/parabank", LoginPage.class);
        AccountsOverviewPage overview = loginPage.login("john", "demo");
        overview.getWelcomeMessage().shouldHave(Condition.text("Welcome John"));
    }

    @Test
    @DisplayName("Wrong password log in")
    @Description("Negative: invalid password – error message")
    public void invalidPasswordTest() {
        LoginPage loginPage = open("https://parabank.parasoft.com/parabank", LoginPage.class);
        loginPage.loginExpectingError("john", "wrongpass");
        loginPage.getErrorMessage().shouldHave(Condition.text("Invalid username or password"));
    }
}
