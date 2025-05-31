package tests.todos;

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

@Epic("Todos")
@Feature("Retrieve User Todos")
public class TC04_RetrieveUserTodos extends TestBase {

    //Variables
    String endpoint = "/users/";
    String title = todoData.getJsonData("todos[0].title");
    String due_on = todoData.getJsonData("todos[0].due_on");
    String status = todoData.getJsonData("todos[0].status");

    //Methods
    @Test(priority = 1, description = "Retrieve user todos")
    @Severity(SeverityLevel.BLOCKER)
    public void retrieveUserTodosTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + userId + "/todos")
                .then()
                .log().all()
                .extract().response();
        //Find the todo with the specified id
        Map<String, Object> todo = response.jsonPath().getMap("find { it.id == " + todoId + " }");
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(todo.get("user_id"), userId);
        Assert.assertEquals(todo.get("title"), title);
        Assert.assertEquals(todo.get("due_on"), due_on);
        Assert.assertEquals(todo.get("status"), status);
    }

    @Test(priority = 2, description = "Retrieve user todos with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveUserTodosWithInvalidTokenTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .when()
                .get(endpoint + userId + "/todos")
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 3, description = "Retrieve user todos with without user ID")
    @Severity(SeverityLevel.NORMAL)
    public void retrieveUserTodosWithoutUserIdTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + "todos")
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }
}
