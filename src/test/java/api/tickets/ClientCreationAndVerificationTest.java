package api.tickets;

import db.VerificationCodeRepository;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class ClientCreationAndVerificationTest {

    public static final String BASE_URL = "https://monberry-dev-crm.cb-dev.ru/";
    public String phoneNumber = generateRussianPhoneNumber();
    public String authToken;

    // Генератор российского номера телефона
    private String generateRussianPhoneNumber() {
        long number = ThreadLocalRandom.current().nextLong(79_000_000_000L, 79_999_999_999L);
        return String.valueOf(number);
    }

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

    @Test
    public void testCreateClient() {
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


        // 2. Верификация телефона и получение токена

            VerificationCodeRepository verificationCodeRepository = new VerificationCodeRepository();
            String verificationCode = verificationCodeRepository.getLatestVerificationCode();

            String verifyPhoneJson = """
        {
            "code": "%s",
            "type": "VERIFICATION",
            "phone": "%s"
        }
        """.formatted(verificationCode, phoneNumber);

            Response verifyPhoneResponse = given().spec(setRequestSpecification())
                    .contentType(ContentType.JSON)
                    .body(verifyPhoneJson)
                    .when()
                    .patch("/api/clients/verify_phone")
                    .then()
                    .statusCode(200)
                    .body("token", notNullValue())
                    .extract().response();

    // authToken = verifyPhoneResponse.jsonPath().getString("data.token");



    // 3. Создание тикета с авторизацией
        @Test
        public void testCreateTicketWithAuth() {
            File photo = new File("src/test/resources/photo3.png");

        given().spec(setRequestSpecification())
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.MULTIPART)
                .accept(ContentType.JSON)
                .formParam("name", "Иванов Иван Иванович")
                .formParam("phone", phoneNumber)
                .formParam("email", "QWERTY@mail.ru")
                .formParam("subject_appeal", "Финансовые обращения")
                .formParam("date_application", "06.04.2025")
                .formParam("text", "Текст обращения")
                .multiPart("file", photo)
                .when()
                .post("/api/tickets/auth_create")
                .then().statusCode(200);

                //.body("success", equalTo(true))
                //.body("ticketId", notNullValue());
    }
}



