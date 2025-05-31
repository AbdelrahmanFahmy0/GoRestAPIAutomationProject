package tests.users;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import tests.base.TestBase;

import java.util.Map;

import static builders.SpecBuilder.createRequestSpecification;
import static io.restassured.RestAssured.given;

@Epic("Users")
@Feature("Retrieve All Users")
public class TC03_RetrieveAllUsers extends TestBase {

    //Variables
    String endpoint = "/users";
    String name = userData.getJsonData("users[0].name");
    String gender = userData.getJsonData("users[0].gender");
    String status = userData.getJsonData("users[0].status");

    //Methods
    @Test(priority = 1, description = "Retrieve all users")
    @Severity(SeverityLevel.BLOCKER)
    public void retrieveAllUsersTC() {
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Find the user with the specified id
        Map<String, Object> user = response.jsonPath().getMap("find { it.id == " + userId + " }");
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(user.get("name"), name);
        Assert.assertEquals(user.get("email"), email);
        Assert.assertEquals(user.get("gender"), gender);
        Assert.assertEquals(user.get("status"), status);
    }

    @Test(priority = 2, description = "Retrieve all users with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveAllUsersWithInvalidTokenTC() {
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .when()
                .get(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }
}
