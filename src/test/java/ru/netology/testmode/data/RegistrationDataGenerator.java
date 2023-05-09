package ru.netology.testmode.data;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Value;
import lombok.val;

import java.util.Locale;

import static io.restassured.RestAssured.given;

public class RegistrationDataGenerator {
    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    private static final Faker faker = new Faker(new Locale("en"));

    private RegistrationDataGenerator() {
    }

    public static void sendRequest(RegistrationData userEntrySent) { /** здесь мы отправляем запрос в нужной конфигурации с готовым объектом пользователя */
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(userEntrySent) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }

    public static String generateLogin() { /** генерируем случайный логин */
        Faker login = new Faker(new Locale("en"));
        String fakerLogin = faker.cat().name();
        fakerLogin = fakerLogin + faker.address().cityName();
        fakerLogin = fakerLogin + faker.numerify("##");
        return fakerLogin.replaceAll(" ", "");
    }

    public static String generatePassword() { /** генерируем случайный пароль */
        Faker login = new Faker(new Locale("en"));
        String fakerPassword = faker.food().sushi().replaceAll("-", "");
        fakerPassword = fakerPassword + faker.relationships().direct();
        fakerPassword = fakerPassword + faker.date().birthday();
        fakerPassword = fakerPassword.replaceAll(":", "");
        return fakerPassword.replaceAll(" ", "");
    }

    public static class Registration {
        private Registration() {
        }

        public static RegistrationData generateUser(String statusConfig) { /** сюда записываются все поля, передаётся статус */
            RegistrationData newUserEntry = new RegistrationData(generateLogin(), generatePassword(), statusConfig);
            return newUserEntry;
        }

        public static RegistrationData registerNewUser(String statusConfig) { /** и здесь, наконец, отправляем запрос со всем нужным,
         вызвав метод с запросом и передав статус в параметре */
            RegistrationData reggedUser = generateUser(statusConfig);
            sendRequest(reggedUser);
            return reggedUser;
        }
    }
}
