package ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;

public class Page {

    private final SelenideElement userField = $x("//input[@data-test='username']");
    private final SelenideElement passwordField = $x("//input[@data-test='password']");
    private final SelenideElement loginButton = $x("//input[@data-test='login-button']");

    public void login(String login, String password) {
        userField.sendKeys(login);
        passwordField.sendKeys(password);
        loginButton.click();
    }

    public void openPage(String url) {
        open(url);
    }

    public void getValue(String locator, String value) {
        $x(locator).sendKeys(value);
    }

    public void clickButton(String locator) {
        $x(locator).click();
    }

    public void checkVisible(String locator) {
        $x(locator).shouldBe(Condition.visible);
    }
}
