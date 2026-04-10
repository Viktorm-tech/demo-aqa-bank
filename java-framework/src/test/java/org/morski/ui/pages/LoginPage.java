package org.morski.ui.pages;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Condition.visible;

public class LoginPage {

    @FindBy(name = "username")
    private SelenideElement usernameInput;

    @FindBy(name = "password")
    private SelenideElement passwordInput;

    @FindBy(css = "input[value='Log In']")
    private SelenideElement loginButton;

    @FindBy(css = ".error")
    private SelenideElement errorMessage;

    public AccountsOverviewPage login(String username, String password) {
        usernameInput.shouldBe(visible).setValue(username);
        passwordInput.setValue(password);
        loginButton.click();
        return new AccountsOverviewPage();
    }

    public void loginExpectingError(String username, String password) {
        usernameInput.shouldBe(visible).setValue(username);
        passwordInput.setValue(password);
        loginButton.click();
    }

    public SelenideElement getErrorMessage() {
        return errorMessage.shouldBe(visible);
    }
}
