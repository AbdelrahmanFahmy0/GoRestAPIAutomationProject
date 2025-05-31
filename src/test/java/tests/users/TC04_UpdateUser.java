package tests.users;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojo.Users;
import tests.base.TestBase;

import java.io.File;
import java.util.Map;

import static builders.SpecBuilder.createRequestSpecification;
import static io.restassured.RestAssured.given;

@Epic("Users")
@Feature("Update User")
public class TC04_UpdateUser extends TestBase {

    //Variables
    String endpoint = "/users/";
    Users users;
    String name = userData.getJsonData("users[1].name");
    String gender = userData.getJsonData("users[0].gender");
    String status = userData.getJsonData("users[1].status");
    File schema = new File("src/test/resources/schemas/userSchema.json");

    //Methods
    @Test(priority = 1, description = "Update user with valid data")
    @Severity(SeverityLevel.BLOCKER)
    public void updateUserWithValidDataTC() throws JsonProcessingException {
        //user data
        users = new Users()
                .setName(name)
                .setEmail(email)
                .setGender(gender)
                .setStatus(status);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .put(endpoint + userId)
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

    @Test(priority = 2, description = "Update user with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserWithInvalidTokenTC() throws JsonProcessingException {
        //Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .body(mapper.writeValueAsString(users))
                .when()
                .put(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 2, description = "Update user without token")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserWithoutTokenTC() throws JsonProcessingException {
        //Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json")))
                .body(mapper.writeValueAsString(users))
                .when()
                .put(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Update user with non-existing ID")
    @Severity(SeverityLevel.NORMAL)
    public void updateUserWithNonExistingIdTC() throws JsonProcessingException {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .put(endpoint + invalidId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Update user without ID")
    @Severity(SeverityLevel.NORMAL)
    public void updateUserWithoutIdTC() throws JsonProcessingException {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .put(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
    }

    @Test(priority = 4, description = "Update user with empty body")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserWithEmptyBodyTC() throws JsonProcessingException {
        users.setName("")
                .setEmail("")
                .setGender("")
                .setStatus("");
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .put(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
        Assert.assertEquals(response.jsonPath().getString("[1].message"), "can't be blank");
        Assert.assertEquals(response.jsonPath().getString("[2].message"), "can't be blank, can be male of female");
        Assert.assertEquals(response.jsonPath().getString("[3].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Update user with empty email")
    @Severity(SeverityLevel.NORMAL)
    public void updateUserWithEmptyEmailTC() throws JsonProcessingException {
        users.setName(name)
                .setEmail("")
                .setGender(gender)
                .setStatus(status);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .put(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Update user with empty name")
    @Severity(SeverityLevel.NORMAL)
    public void updateUserWithEmptyNameTC() throws JsonProcessingException {
        users.setName("")
                .setEmail(email)
                .setGender(gender)
                .setStatus(status);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .put(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Update user with empty gender")
    @Severity(SeverityLevel.NORMAL)
    public void updateUserWithEmptyGenderTC() throws JsonProcessingException {
        users.setName(name)
                .setEmail(email)
                .setGender("")
                .setStatus(status);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .put(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank, can be male of female");
    }

    @Test(priority = 4, description = "Update user with empty status")
    @Severity(SeverityLevel.NORMAL)
    public void updateUserWithEmptyStatusTC() throws JsonProcessingException {
        users.setName(name)
                .setEmail(email)
                .setGender(gender)
                .setStatus("");
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .put(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Update user with invalid email")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserWithInvalidEmailTC() throws JsonProcessingException {
        users.setName(name)
                .setEmail("user123.com")
                .setGender(gender)
                .setStatus(status);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .put(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "is invalid");
    }

    @Test(priority = 4, description = "Update user with existed email")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserWithExistedEmailTC() throws JsonProcessingException {
        users.setName(name)
                .setEmail("user2025-05-28-21-21-34@example.com")
                .setGender(gender)
                .setStatus(status);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .put(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "has already been taken");
    }

    @Test(priority = 4, description = "Update user with invalid status")
    @Severity(SeverityLevel.NORMAL)
    public void updateUserWithInvalidStatusTC() throws JsonProcessingException {
        users.setName(name)
                .setEmail(email)
                .setGender(gender)
                .setStatus("invalidStatus");
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .put(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }
}