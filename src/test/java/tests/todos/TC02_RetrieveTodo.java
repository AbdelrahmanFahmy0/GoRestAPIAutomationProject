package tests.todos;

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

@Epic("Todos")
@Feature("Retrieve Todo")
public class TC02_RetrieveTodo extends TestBase {

    //Variables
    String endpoint = "/todos/";
    String title = todoData.getJsonData("todos[0].title");
    String due_on = todoData.getJsonData("todos[0].due_on");
    String status = todoData.getJsonData("todos[0].status");
    String updatedStatus = todoData.getJsonData("todos[1].status");
    String partialUpdatedStatus = todoData.getJsonData("todos[2].status");
    File schema = new File("src/test/resources/schemas/todoSchema.json");

    //Methods
    @Test(priority = 1, description = "Retrieve a todo")
    @Severity(SeverityLevel.BLOCKER)
    public void retrieveTodoTC() {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + todoId)
                .then()
                .log().all()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schema))
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getInt("user_id"), userId);
        Assert.assertEquals(response.jsonPath().getString("title"), title);
        Assert.assertEquals(response.jsonPath().getString("due_on"), due_on);
        Assert.assertEquals(response.jsonPath().getString("status"), status);
    }

    @Test(priority = 2, description = "Retrieve a todo with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveTodoWithInvalidTokenTC() {
        // Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .when()
                .get(endpoint + todoId)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 2, description = "Retrieve a todo without token")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveTodoWithoutTokenTC() {
        // Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json")))
                .when()
                .get(endpoint + todoId)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Retrieve a todo with non-existing ID")
    @Severity(SeverityLevel.NORMAL)
    public void retrieveTodoWithNonExistingIdTC() {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + invalidId)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 4, description = "Retrieve a todo after update")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveTodoAfterUpdateTC() {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + todoId)
                .then()
                .log().all()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schema))
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getInt("user_id"), userId);
        Assert.assertEquals(response.jsonPath().getString("title"), title);
        Assert.assertEquals(response.jsonPath().getString("due_on"), due_on);
        Assert.assertEquals(response.jsonPath().getString("status"), updatedStatus);
    }

    @Test(priority = 5, description = "Retrieve a todo after partial update")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveTodoAfterPartialUpdateTC() {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + todoId)
                .then()
                .log().all()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schema))
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getInt("user_id"), userId);
        Assert.assertEquals(response.jsonPath().getString("title"), title);
        Assert.assertEquals(response.jsonPath().getString("due_on"), due_on);
        Assert.assertEquals(response.jsonPath().getString("status"), partialUpdatedStatus);
    }

    @Test(priority = 6, description = "Retrieve a todo after deletion")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveTodoAfterDeletionTC() {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + todoId)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }
}
