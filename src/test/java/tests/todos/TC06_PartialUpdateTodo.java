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
@Feature("Partial Update Todo")
public class TC06_PartialUpdateTodo extends TestBase {

    //Variables
    String endpoint = "/todos/";
    Todos todo;
    String title = todoData.getJsonData("todos[0].title");
    String due_on = todoData.getJsonData("todos[0].due_on");
    String status = todoData.getJsonData("todos[2].status");
    File schema = new File("src/test/resources/schemas/todoSchema.json");

    //Methods
    @Test(priority = 1, description = "Partial Update Todo")
    @Severity(SeverityLevel.BLOCKER)
    public void partialUpdateTodoTC() throws JsonProcessingException {
        // Todo data
        todo = new Todos()
                .setStatus(status);
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(todo))
                .when()
                .patch(endpoint + todoId)
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

    @Test(priority = 2, description = "Partial Update Todo with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void partialUpdateTodoWithInvalidTokenTC() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .body(mapper.writeValueAsString(todo))
                .when()
                .patch(endpoint + todoId)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 2, description = "Partial Update Todo without token")
    @Severity(SeverityLevel.CRITICAL)
    public void partialUpdateTodoWithoutTokenTC() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json")))
                .body(mapper.writeValueAsString(todo))
                .when()
                .patch(endpoint + todoId)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Partial Update Todo with non-existing ID")
    @Severity(SeverityLevel.NORMAL)
    public void partialUpdateTodoWithNonExistingIdTC() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(todo))
                .when()
                .patch(endpoint + invalidId)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Partial Update Todo without ID")
    @Severity(SeverityLevel.NORMAL)
    public void partialUpdateTodoWithoutIdTC() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(todo))
                .when()
                .patch(endpoint)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
    }

    @Test(priority = 4, description = "Partial Update Todo with empty title")
    @Severity(SeverityLevel.CRITICAL)
    public void partialUpdateTodoWithEmptyTitleTC() throws JsonProcessingException {
        // Todo data
        todo = new Todos().setTitle("");
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(todo))
                .when()
                .patch(endpoint + todoId)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Partial Update Todo with empty status")
    @Severity(SeverityLevel.BLOCKER)
    public void partialUpdateTodoWithEmptyStatusTC() throws JsonProcessingException {
        // Todo data
        todo = new Todos().setStatus("");
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(todo))
                .when()
                .patch(endpoint + todoId)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank, can be pending or completed");
    }

    @Test(priority = 4, description = "Partial Update Todo with invalid status")
    @Severity(SeverityLevel.CRITICAL)
    public void partialUpdateTodoWithInvalidStatusTC() throws JsonProcessingException {
        // Todo data
        todo = new Todos().setStatus("invalid_status");
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(todo))
                .when()
                .patch(endpoint + todoId)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank, can be pending or completed");
    }
}
