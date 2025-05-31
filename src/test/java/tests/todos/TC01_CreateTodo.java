package tests.todos;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojo.Todos;
import tests.base.TestBase;

import java.io.File;
import java.util.Map;

import static builders.SpecBuilder.createRequestSpecification;
import static io.restassured.RestAssured.given;

@Epic("Todos")
@Feature("Create Todo")
public class TC01_CreateTodo extends TestBase {

    //Variables
    String endpoint = "/users/";
    Todos todo;
    String title = todoData.getJsonData("todos[0].title");
    String due_on = todoData.getJsonData("todos[0].due_on");
    String status = todoData.getJsonData("todos[0].status");
    File schema = new File("src/test/resources/schemas/todoSchema.json");

    //Methods
    @Test(priority = 1, description = "Create a new todo")
    @Severity(SeverityLevel.BLOCKER)
    public void createTodoTC() throws JsonProcessingException {
        // Comment data
        todo = new Todos()
                .setTitle(title)
                .setDue_on(due_on)
                .setStatus(status);
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(todo))
                .when()
                .post(endpoint + userId + "/todos")
                .then()
                .log().all()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schema))
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 201);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getInt("user_id"), userId);
        Assert.assertEquals(response.jsonPath().getString("title"), title);
        Assert.assertEquals(response.jsonPath().getString("due_on"), due_on);
        Assert.assertEquals(response.jsonPath().getString("status"), status);

        // Get to-do ID
        todoId = response.jsonPath().getInt("id");
    }

    @Test(priority = 2, description = "Create a new todo with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void createTodoWithInvalidTokenTC() throws JsonProcessingException {
        // Request with invalid token
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .body(mapper.writeValueAsString(todo))
                .when()
                .post(endpoint + userId + "/todos")
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 2, description = "Create a new todo without token")
    @Severity(SeverityLevel.CRITICAL)
    public void createTodoWithoutTokenTC() throws JsonProcessingException {
        // Request without token
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json")))
                .body(mapper.writeValueAsString(todo))
                .when()
                .post(endpoint + userId + "/todos")
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Authentication failed");
    }

    @Test(priority = 3, description = "Create a new todo with non-existing user ID")
    @Severity(SeverityLevel.NORMAL)
    public void createTodoWithNonExistingUserIdTC() throws JsonProcessingException {
        // Request with non-existing user ID
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(todo))
                .when()
                .post(endpoint + invalidId + "/todos")
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "must exist");
    }

    @Test(priority = 3, description = "Create a new todo without post ID")
    @Severity(SeverityLevel.CRITICAL)
    public void createTodoWithoutPostIdTC() throws JsonProcessingException {
        // Request without post ID
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(todo))
                .when()
                .post(endpoint + "todos")
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
    }

    @Test(priority = 4, description = "Create a new todo without body")
    @Severity(SeverityLevel.CRITICAL)
    public void createTodoWithoutBodyTC() {
        // Request without body
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .post(endpoint + userId + "/todos")
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
        Assert.assertEquals(response.jsonPath().getString("[1].message"), "can't be blank, can be pending or completed");
    }

    @Test(priority = 4, description = "Create a new todo with empty title")
    @Severity(SeverityLevel.CRITICAL)
    public void createTodoWithEmptyTitleTC() throws JsonProcessingException {
        // Comment data with empty title
        todo = new Todos()
                .setTitle("")
                .setDue_on(due_on)
                .setStatus(status);
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(todo))
                .when()
                .post(endpoint + userId + "/todos")
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Create a new todo with empty status")
    @Severity(SeverityLevel.CRITICAL)
    public void createTodoWithEmptyStatusTC() throws JsonProcessingException {
        // Comment data with empty status
        todo = new Todos()
                .setTitle(title)
                .setDue_on(due_on)
                .setStatus("");
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(todo))
                .when()
                .post(endpoint + userId + "/todos")
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank, can be pending or completed");
    }

    @Test(priority = 5, description = "Create a new todo with invalid status")
    @Severity(SeverityLevel.CRITICAL)
    public void createTodoWithInvalidStatusTC() throws JsonProcessingException {
        // Comment data with invalid status
        todo = new Todos()
                .setTitle(title)
                .setDue_on(due_on)
                .setStatus("invalid_status");
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(todo))
                .when()
                .post(endpoint + userId + "/todos")
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank, can be pending or completed");
    }
}
