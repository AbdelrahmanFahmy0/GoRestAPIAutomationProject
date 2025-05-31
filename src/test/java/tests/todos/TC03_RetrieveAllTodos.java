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
@Feature("Retrieve All Todos")
public class TC03_RetrieveAllTodos extends TestBase {

    //Variables
    String endpoint = "/todos";
    String title = todoData.getJsonData("todos[0].title");
    String due_on = todoData.getJsonData("todos[0].due_on");
    String status = todoData.getJsonData("todos[0].status");

    //Methods
    @Test(priority = 1, description = "Retrieve all todos")
    @Severity(SeverityLevel.BLOCKER)
    public void retrieveAllTodosTC() {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint)
                .then()
                .log().all()
                .assertThat()
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

    @Test(priority = 2, description = "Retrieve all todos with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveAllTodosWithInvalidTokenTC() {
        // Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .when()
                .get(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }
}
