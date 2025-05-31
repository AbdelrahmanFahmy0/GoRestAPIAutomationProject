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
@Feature("Partial Update User")
public class TC05_PartialUpdateUser extends TestBase {

    //Variables
    String endpoint = "/users/";
    Users users;
    String name = userData.getJsonData("users[2].name");
    String gender = userData.getJsonData("users[0].gender");
    String status = userData.getJsonData("users[1].status");
    File schema = new File("src/test/resources/schemas/userSchema.json");

    //Methods
    @Test(priority = 1, description = "Partial update user")
    @Severity(SeverityLevel.BLOCKER)
    public void partialUpdateUser() throws JsonProcessingException {
        users = new Users();
        users.setName(name);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .patch(endpoint + userId)
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

    @Test(priority = 2, description = "Partial update user with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void partialUpdateUserWithInvalidToken() throws JsonProcessingException {
        users = new Users();
        users.setName(name);
        //Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .body(mapper.writeValueAsString(users))
                .when()
                .patch(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 2, description = "Partial update user without token")
    @Severity(SeverityLevel.CRITICAL)
    public void partialUpdateUserWithoutToken() throws JsonProcessingException {
        users = new Users();
        users.setName(name);
        //Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json")))
                .body(mapper.writeValueAsString(users))
                .when()
                .patch(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Partial update user with non-existing ID")
    @Severity(SeverityLevel.NORMAL)
    public void partialUpdateUserWithNonExistingId() throws JsonProcessingException {
        users = new Users();
        users.setName(name);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .patch(endpoint + invalidId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Partial update user without ID")
    @Severity(SeverityLevel.NORMAL)
    public void partialUpdateUserWithoutId() throws JsonProcessingException {
        users = new Users();
        users.setName(name);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .patch(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
    }

    @Test(priority = 4, description = "Partial update user with empty name")
    @Severity(SeverityLevel.NORMAL)
    public void partialUpdateUserWithEmptyName() throws JsonProcessingException {
        users = new Users();
        users.setName("");
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .patch(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Partial update user empty email")
    @Severity(SeverityLevel.NORMAL)
    public void partialUpdateUserWithEmptyEmail() throws JsonProcessingException {
        users = new Users();
        users.setEmail("");
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .patch(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Partial update user with empty gender")
    @Severity(SeverityLevel.NORMAL)
    public void partialUpdateUserWithEmptyGender() throws JsonProcessingException {
        users = new Users();
        users.setGender("");
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .patch(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank, can be male of female");
    }

    @Test(priority = 4, description = "Partial update user with empty status")
    @Severity(SeverityLevel.NORMAL)
    public void partialUpdateUserWithEmptyStatus() throws JsonProcessingException {
        users = new Users();
        users.setStatus("");
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .patch(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Partial update user with invalid email")
    @Severity(SeverityLevel.CRITICAL)
    public void partialUpdateUserWithInvalidEmail() throws JsonProcessingException {
        users = new Users();
        users.setEmail("user123.com");
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .patch(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "is invalid");
    }

    @Test(priority = 4, description = "Partial update user with existed email")
    @Severity(SeverityLevel.CRITICAL)
    public void partialUpdateUserWithExistedEmail() throws JsonProcessingException {
        users = new Users();
        users.setEmail("user2025-05-28-21-21-34@example.com");
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .patch(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "has already been taken");
    }

    @Test(priority = 4, description = "Partial update user with invalid status")
    @Severity(SeverityLevel.NORMAL)
    public void partialUpdateUserWithInvalidStatus() throws JsonProcessingException {
        users = new Users();
        users.setStatus("invalid_status");
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(users))
                .when()
                .patch(endpoint + userId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }
}
