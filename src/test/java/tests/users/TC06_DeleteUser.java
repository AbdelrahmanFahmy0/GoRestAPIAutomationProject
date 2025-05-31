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
@Feature("Delete User")
public class TC06_DeleteUser extends TestBase {

    //Variables
    String endpoint = "/users/";

    //Methods
    @Test(priority = 1, description = "Delete a user")
    @Severity(SeverityLevel.BLOCKER)
    public void deleteUserTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .delete(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 204);
        Assert.assertTrue(response.getTime() < 3000);
    }

    @Test(priority = 2, description = "Delete a user with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteUserWithInvalidTokenTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .when()
                .delete(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 2, description = "Delete a user without token")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteUserWithoutTokenTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json")))
                .when()
                .delete(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Delete a user with non-existing ID")
    @Severity(SeverityLevel.NORMAL)
    public void deleteUserWithNonExistingIdTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .delete(endpoint + invalidId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Delete a user without ID")
    @Severity(SeverityLevel.NORMAL)
    public void deleteUserWithoutIdTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .delete(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
    }
}
