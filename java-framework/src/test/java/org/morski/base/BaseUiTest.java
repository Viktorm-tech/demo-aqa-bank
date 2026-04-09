package org.morski.base;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseUiTest {

    @BeforeAll
    public static void setup() {
        Configuration.baseUrl = "https://parabank.parasoft.com/parabank";
        Configuration.browser = "chrome";
        Configuration.headless = Boolean.parseBoolean(
                System.getProperty("selenide.headless", "true")
        );
        Configuration.timeout = 10000;
    }
}
