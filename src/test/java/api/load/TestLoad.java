package api.load;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;

public class TestLoad {

    public static final String BASE_URL = "https://monberry-dev-crm.cb-dev.ru/";

    public static RequestSpecification setRequestSpecification() {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBaseUri(BASE_URL);
        builder.addFilters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()));
        builder.setContentType(ContentType.JSON.toString());
        return builder.build();
    }

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    // Генератор российского номера телефона
    private String generateRussianPhoneNumber() {
        long number = ThreadLocalRandom.current().nextLong(79_000_000_000L, 79_999_999_999L);
        return String.valueOf(number);
    }

    @Test
    public void testCreateClient() {
        for (int i = 0; i < 5; i++) {
            // Генерация нового номера телефона для каждого цикла
            String phoneNumber = generateRussianPhoneNumber();

            // 1. Создание клиента
            String createClientJson = """
                    {
                        "phone": "%s",
                        "amount": 7000,
                        "period": 5,
                        "tariff_id": 7,
                        "client_data": {
                            "client": {
                                "phone": "%s",
                                "last_name": "Иванов",
                                "first_name": "Иван",
                                "middle_name": "Иванович"
                            },
                            "contacts": {
                                "email": "QWERTY@mail.ru",
                                "sex": "Мужской"
                            }
                        },
                        "utm": {
                            "_ym_uid": "1743409457659607131",
                            "_ym_d": "1743409457",
                            "_ym_isad": "2",
                            "_ym_visorc": "w",
                            "from_crm": "false"
                        },
                        "timezone": 3,
                        "insurance_docs_used": null
                    }
                    """.formatted(phoneNumber, phoneNumber);

            Response createClientResponse = given().spec(setRequestSpecification())
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(createClientJson)
                    .when()
                    .post("/api/clients/create")
                    .then()
                    .statusCode(200)
                    //.body("success", equalTo(true))
                    .extract().response();
        }
    }
}

