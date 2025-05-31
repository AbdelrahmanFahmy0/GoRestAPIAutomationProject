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
@Feature("Update Todo")
public class TC05_UpdateTodo extends TestBase {

    //Variables
    String endpoint = "/todos/";
    Todos todo;
    String title = todoData.getJsonData("todos[0].title");
    String due_on = todoData.getJsonData("todos[0].due_on");
    String status = todoData.getJsonData("todos[1].status");
    File schema = new File("src/test/resources/schemas/todoSchema.json");

    //Methods
    @Test(priority = 1, description = "Update a todo")
    @Severity(SeverityLevel.BLOCKER)
    public void updateTodo() throws JsonProcessingException {
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
                .put(endpoint + todoId)
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

    @Test(priority = 2, description = "Update a todo with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void updateTodoWithInvalidToken() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .body(mapper.writeValueAsString(todo))
                .when()
                .put(endpoint + todoId)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 2, description = "Update a todo without token")
    @Severity(SeverityLevel.CRITICAL)
    public void updateTodoWithoutToken() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json")))
                .body(mapper.writeValueAsString(todo))
                .when()
                .put(endpoint + todoId)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Update a todo with non-existing todo ID")
    @Severity(SeverityLevel.NORMAL)
    public void updateTodoWithNonExistingId() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(todo))
                .when()
                .put(endpoint + invalidId)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Update a todo without ID")
    @Severity(SeverityLevel.NORMAL)
    public void updateTodoWithoutId() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(todo))
                .when()
                .put(endpoint)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
    }

    @Test(priority = 4, description = "Update a todo with empty body")
    @Severity(SeverityLevel.CRITICAL)
    public void updateTodoWithEmptyBody() throws JsonProcessingException {
        // Comment data
        todo = new Todos()
                .setTitle("")
                .setDue_on("")
                .setStatus("");
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(todo))
                .when()
                .put(endpoint + todoId)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
        Assert.assertEquals(response.jsonPath().getString("[1].message"), "can't be blank, can be pending or completed");
    }

    @Test(priority = 4, description = "Update a todo with empty title")
    @Severity(SeverityLevel.CRITICAL)
    public void updateTodoWithEmptyTitle() throws JsonProcessingException {
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
                .put(endpoint + todoId)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Update a todo with empty status")
    @Severity(SeverityLevel.CRITICAL)
    public void updateTodoWithEmptyStatus() throws JsonProcessingException {
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
                .put(endpoint + todoId)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank, can be pending or completed");
    }

    @Test(priority = 5, description = "Update a todo with invalid status")
    @Severity(SeverityLevel.CRITICAL)
    public void updateTodoWithInvalidStatus() throws JsonProcessingException {
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
                .put(endpoint + todoId)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank, can be pending or completed");
    }
}
