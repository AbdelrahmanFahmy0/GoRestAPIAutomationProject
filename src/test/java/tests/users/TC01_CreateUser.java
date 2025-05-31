package tests.users;

import io.qameta.allure.*;
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
import static utils.TimestampUtils.getTimestamp;

@Epic("Users")
@Feature("Create User")
public class TC01_CreateUser extends TestBase {

    //Variables
    String endpoint = "/users";
    Users users;
    String name = userData.getJsonData("users[0].name");
    String gender = userData.getJsonData("users[0].gender");
    String status = userData.getJsonData("users[0].status");
    File schema = new File("src/test/resources/schemas/userSchema.json");

    //Methods
    @Test(priority = 1, description = "Create a new user")
    @Severity(SeverityLevel.BLOCKER)
    public void createNewUserTC() throws JsonProcessingException {
        email = "user" + getTimestamp() + "@example.com";
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
                .post(endpoint)
                .then()
                .log().all()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schema))
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 201);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("name"), name);
        Assert.assertEquals(response.jsonPath().getString("email"), email);
        Assert.assertEquals(response.jsonPath().getString("gender"), gender);
        Assert.assertEquals(response.jsonPath().getString("status"), status);
        //Get user ID
        userId = response.jsonPath().getInt("id");
    }

    @Test(priority = 2, description = "Create a new user without token")
    @Severity(SeverityLevel.CRITICAL)
    public void createNewUserWithoutTokenTC() throws JsonProcessingException {
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json")))
                .body(mapper.writeValueAsString(users))
                .when()
                .post(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Authentication failed");
    }

    @Test(priority = 2, description = "Create a new user with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void createNewUserWithInvalidTokenTC() throws JsonProcessingException {
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .body(mapper.writeValueAsString(users))
                .when()
                .post(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 3, description = "Create a new user without body")
    @Severity(SeverityLevel.CRITICAL)
    public void createNewUserWithoutBodyTC() {
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .post(endpoint)
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

    @Test(priority = 3, description = "Create a new user without name")
    @Severity(SeverityLevel.CRITICAL)
    public void createNewUserWithoutNameTC() throws JsonProcessingException {
        users.setName("")
                .setEmail(email)
                .setGender(gender)
                .setStatus(status);
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .post(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 3, description = "Create a new user without email")
    @Severity(SeverityLevel.CRITICAL)
    public void createNewUserWithoutEmailTC() throws JsonProcessingException {
        users.setName(name)
                .setEmail("")
                .setGender(gender)
                .setStatus(status);
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .post(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 3, description = "Create a new user without gender")
    @Severity(SeverityLevel.CRITICAL)
    public void createNewUserWithoutGenderTC() throws JsonProcessingException {
        users.setName(gender)
                .setEmail(email)
                .setGender("")
                .setStatus(status);
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .post(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank, can be male of female");
    }

    @Test(priority = 3, description = "Create a new user without status")
    @Severity(SeverityLevel.CRITICAL)
    public void createNewUserWithoutStatusTC() throws JsonProcessingException {
        users.setName(name)
                .setEmail(email)
                .setGender(gender)
                .setStatus("");
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .post(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Create a new user with existed email")
    @Severity(SeverityLevel.CRITICAL)
    public void createNewUserWithExistedEmailTC() throws JsonProcessingException {
        users.setName(name)
                .setEmail(email)
                .setGender(gender)
                .setStatus(status);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .post(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "has already been taken");
    }

    @Test(priority = 4, description = "Create a new user with invalid email")
    @Severity(SeverityLevel.CRITICAL)
    public void createNewUserWithInvalidEmailTC() throws JsonProcessingException {
        users.setName(name)
                .setEmail("user123.com")
                .setGender(gender)
                .setStatus(status);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .post(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "is invalid");
    }

    @Test(priority = 4, description = "Create a new user with invalid gender")
    @Severity(SeverityLevel.NORMAL)
    public void createNewUserWithInvalidGenderTC() throws JsonProcessingException {
        users.setName(name)
                .setEmail(email)
                .setGender("invalidGender")
                .setStatus(status);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .post(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank, can be male of female");
    }

    @Test(priority = 4, description = "Create a new user with invalid status")
    @Severity(SeverityLevel.NORMAL)
    public void createNewUserWithInvalidStatusTC() throws JsonProcessingException {
        users.setName(name)
                .setEmail(email)
                .setGender(gender)
                .setStatus("invalidStatus");
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .post(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }
}