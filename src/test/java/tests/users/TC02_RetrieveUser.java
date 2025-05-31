package tests.users;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import tests.base.TestBase;

import java.io.File;
import java.util.Map;

import static builders.SpecBuilder.createRequestSpecification;
import static io.restassured.RestAssured.given;

@Epic("Users")
@Feature("Retrieve User")
public class TC02_RetrieveUser extends TestBase {

    //Variables
    String endpoint = "/users/";
    String name = userData.getJsonData("users[0].name");
    String gender = userData.getJsonData("users[0].gender");
    String status = userData.getJsonData("users[0].status");
    String updatedName = userData.getJsonData("users[1].name");
    String updatedStatus = userData.getJsonData("users[1].status");
    String partialUpdateName = userData.getJsonData("users[2].name");
    File schema = new File("src/test/resources/schemas/userSchema.json");

    //Methods
    @Test(priority = 1, description = "Retrieve a user")
    @Severity(SeverityLevel.BLOCKER)
    public void retrieveUserTC() {
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + userId)
                .then()
                .log().all()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schema))
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("name"), name);
        Assert.assertEquals(response.jsonPath().getString("email"), email);
        Assert.assertEquals(response.jsonPath().getString("gender"), gender);
        Assert.assertEquals(response.jsonPath().getString("status"), status);
    }

    @Test(priority = 2, description = "Retrieve a user without token")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveUserWithoutTokenTC() {
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json")))
                .when()
                .get(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 2, description = "Retrieve a user with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveUserWithInvalidTokenTC() {
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .when()
                .get(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 3, description = "Retrieve a user with non-existing ID")
    @Severity(SeverityLevel.NORMAL)
    public void retrieveUserWithNonExistingIdTC() {
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + invalidId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 4, description = "Retrieve a user after update")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveUserAfterUpdateTC() {
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + userId)
                .then()
                .log().all()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schema))
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("name"), updatedName);
        Assert.assertEquals(response.jsonPath().getString("email"), email);
        Assert.assertEquals(response.jsonPath().getString("gender"), gender);
        Assert.assertEquals(response.jsonPath().getString("status"), updatedStatus);
    }

    @Test(priority = 5, description = "Retrieve a user after partial update")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveUserAfterPartialUpdateTC() {
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + userId)
                .then()
                .log().all()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schema))
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("name"), partialUpdateName);
        Assert.assertEquals(response.jsonPath().getString("email"), email);
        Assert.assertEquals(response.jsonPath().getString("gender"), gender);
        Assert.assertEquals(response.jsonPath().getString("status"), updatedStatus);
    }

    @Test(priority = 6, description = "Retrieve a user after deletion")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveUserAfterDeleteTC() {
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }
}
