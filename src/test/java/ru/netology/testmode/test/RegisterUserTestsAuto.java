package ru.netology.testmode.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.testmode.data.RegistrationData;
import ru.netology.testmode.data.RegistrationDataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;

public class RegisterUserTestsAuto {

    @Test
    @DisplayName("Case 0: If no user matches login data")
    void shdNotLoginIfUserUnreggedActive() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
        // пропускаем отправку запроса
        RegistrationData newUser = RegistrationDataGenerator.Registration.generateUser("active");
        $x("//span[@data-test-id='login']/descendant::input[@name='login']").setValue(newUser.getLogin());
        $x("//span[@data-test-id='password']/descendant::input[@name='password']").setValue(newUser.getPassword());
        $x("//button[@data-test-id='action-login']/descendant::span[text()='Продолжить']").click();
        // должно появиться уведомление об ошибке логина/пароля
        $x("//div[@data-test-id='error-notification']/descendant::div[text()='Неверно указан логин или пароль']").should(Condition.appear);
    }

    @Test
    @DisplayName("Case 1: For active, valid login, valid password")
    void shdLoginIfUserReggedAndActiveWithValidPassword() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
        // теперь запрос отправляется
        RegistrationData newUser = RegistrationDataGenerator.Registration.registerNewUser("active");
        $x("//span[@data-test-id='login']/descendant::input[@name='login']").setValue(newUser.getLogin());
        $x("//span[@data-test-id='password']/descendant::input[@name='password']").setValue(newUser.getPassword());
        $x("//button[@data-test-id='action-login']/descendant::span[text()='Продолжить']").click();
        $x("//h2[contains(text(),'Личный кабинет')]").should(Condition.appear); // должен произойти переход в личный кабинет
    }

    @Test
    @DisplayName("Case 2: For active, valid login, wrong password")
    void shdNotLoginIfUserReggedAndActiveButInvalidPassword() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
        // запрос отправляется
        RegistrationData newUser = RegistrationDataGenerator.Registration.registerNewUser("active");
        // создаём второго пользователя для некорректного пароля
        RegistrationData newErUser = RegistrationDataGenerator.Registration.generateUser("active");
        $x("//span[@data-test-id='login']/descendant::input[@name='login']").setValue(newUser.getLogin());
        // вводим пароль от второго пользователя
        $x("//span[@data-test-id='password']/descendant::input[@name='password']").setValue(newErUser.getPassword());
        $x("//button[@data-test-id='action-login']/descendant::span[text()='Продолжить']").click();
        // должно появиться уведомление об ошибке логина/пароля
        $x("//div[@data-test-id=\"error-notification\"]/descendant::div[text()='Неверно указан логин или пароль']").should(Condition.appear);
    }

    @Test
    @DisplayName("Case 3: For active with invalid login")
    void shdNotLoginIfUserReggedAndActiveButInvalidLogin() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
        // запрос отправляется
        RegistrationData newUser = RegistrationDataGenerator.Registration.registerNewUser("active");
        // создаём второго пользователя для некорректного пароля
        RegistrationData newErUser = RegistrationDataGenerator.Registration.generateUser("active");
        $x("//span[@data-test-id='login']/descendant::input[@name='login']").setValue(newErUser.getLogin());
        // вводим пароль от второго пользователя
        $x("//span[@data-test-id='password']/descendant::input[@name='password']").setValue(newUser.getPassword());
        $x("//button[@data-test-id='action-login']/descendant::span[text()='Продолжить']").click();
        // должно появиться уведомление об ошибке логина/пароля
        $x("//div[@data-test-id=\"error-notification\"]/descendant::div[text()='Неверно указан логин или пароль']").should(Condition.appear);
    }

    @Test
    @DisplayName("Case 4: Blocked, both login and password correct")
    void shdNotLoginIfReggedButBlockedWithValidPair() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
        // запрос отправляется, создаётся заблокированный пользователь (что бы это ни значило)
        RegistrationData newUser = RegistrationDataGenerator.Registration.registerNewUser("blocked");
        $x("//span[@data-test-id='login']/descendant::input[@name='login']").setValue(newUser.getLogin());
        $x("//span[@data-test-id='password']/descendant::input[@name='password']").setValue(newUser.getPassword());
        $x("//button[@data-test-id='action-login']/descendant::span[text()='Продолжить']").click();
        // должно появиться уведомление о блокировке
        $x("//div[@data-test-id='error-notification']/descendant::div[text()='Пользователь заблокирован']").should(Condition.appear);
    }

    @Test
    @DisplayName("Case 5: Blocked, wrong login")
    void shdNotLoginIfReggedButBlockedWithInvalidLoginAndValidPassword() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
        // запрос отправляется, создаётся заблокированный пользователь
        RegistrationData newUser = RegistrationDataGenerator.Registration.registerNewUser("blocked");
        // создаём второго пользлвателя для некорректных данных
        RegistrationData newErUser = RegistrationDataGenerator.Registration.generateUser("active");

        $x("//span[@data-test-id='login']/descendant::input[@name='login']").setValue(newErUser.getLogin()); // неверный логин
        $x("//span[@data-test-id='password']/descendant::input[@name='password']").setValue(newUser.getPassword());
        $x("//button[@data-test-id='action-login']/descendant::span[text()='Продолжить']").click();
        // должно появиться уведомление об ошибке логина/пароля
        $x("//div[@data-test-id='error-notification']/descendant::div[text()='Неверно указан логин или пароль']").should(Condition.appear);
    }

    @Test
    @DisplayName("Case 6: Blocked, with right login and wrong password")
    void shdNotLoginIfReggedButBlockedWithValidLoginAndInValidPassword() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
        // запрос отправляется, создаётся заблокированный пользователь
        RegistrationData newUser = RegistrationDataGenerator.Registration.registerNewUser("blocked");
        // создаём второго пользователя для некорректных данных
        RegistrationData newErUser = RegistrationDataGenerator.Registration.generateUser("active");

        $x("//span[@data-test-id='login']/descendant::input[@name='login']").setValue(newUser.getLogin());
        $x("//span[@data-test-id='password']/descendant::input[@name='password']").setValue(newErUser.getPassword()); // неверный пароль
        $x("//button[@data-test-id='action-login']/descendant::span[text()='Продолжить']").click();
        // должно появиться уведомление об ошибке логина/пароля
        $x("//div[@data-test-id='error-notification']/descendant::div[text()='Неверно указан логин или пароль']").should(Condition.appear);
    }
}
