package api.test;

import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;

public class ApiTest {
@Test
public void getAllUsersTest(){
    given().get("https://fakestoreapi.com/users")
            .then()
            .log().all()
            .statusCode(200);

}
@Test
public  void  getSimpleUserTest(){
    int userId = 5;
    given().pathParam("userId", userId)
            .get("https://fakestoreapi.com/users/{userId}")
            .then().log().all()
            .statusCode(200)
            .body("id", equalTo(userId))
            .body("address.zipcode", matchesPattern("\\d{5}-\\d{4}"));
}

@Test
public void getAllUsersWithLimitTests(){
int limitSize = 3;
given().queryParam("limit", limitSize)
        .get("https://fakestoreapi.com/docs#tag/Users")
        .then().log().all()
    .statusCode(200);
}

@Test
public void getAllUsersSortByDecsTest(){
    String sortType = "desc";
    Response sortedResponse =  given().queryParam("sort", sortType)
            .get("https://fakestoreapi.com/users")
            .then().log().all()
                    .extract().response();

    Response notSortedResponse = given().get("https://fakestoreapi.com/users")
            .then().log().all().extract().response();

    List<Integer> sortedResponseIds = sortedResponse.jsonPath().getList("id");
    List<Integer> notSortedResponseIds = notSortedResponse.jsonPath().getList("id");

    List<Integer> sortedByCode = notSortedResponseIds
            .stream()
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());

    Assertions.assertNotEquals(sortedResponseIds,notSortedResponseIds);
    Assertions.assertEquals(sortedByCode, sortedResponseIds);





}

}
