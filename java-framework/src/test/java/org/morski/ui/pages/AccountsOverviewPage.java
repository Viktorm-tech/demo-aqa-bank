package org.morski.ui.pages;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.support.FindBy;

@Getter
public class AccountsOverviewPage {

    @FindBy(css = "h1.title")
    private SelenideElement welcomeMessage;

}
