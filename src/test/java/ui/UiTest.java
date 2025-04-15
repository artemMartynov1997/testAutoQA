package ui;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;

public class UiTest {
    Page page = new Page();

    @Test
    public void openSwagLabs() {
//        open("https://www.saucedemo.com/");
//        $x("//input[@data-test='username']").sendKeys("standard_user");
//        $x("//input[@data-test='password']").sendKeys("secret_sauce");
//        $x("//input[@data-test='login-button']").click();
//        $x("//div[. = 'Swag Labs']").shouldBe(Condition.visible);
        page.openPage("https://www.saucedemo.com/");
        page.login("standard_user", "secret_sauce");
        page.checkVisible("//div[. = 'Swag Labs']");
    }
}
