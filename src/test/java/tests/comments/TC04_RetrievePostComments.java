package tests.comments;

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

@Epic("Comments")
@Feature("Retrieve Post Comments")
public class TC04_RetrievePostComments extends TestBase {

    //Variables
    String endpoint = "/posts/";
    String name = commentData.getJsonData("comments[0].name");
    String email = commentData.getJsonData("comments[0].email");
    String body = commentData.getJsonData("comments[0].body");

    //Methods
    @Test(priority = 1, description = "Retrieve post comments")
    @Severity(SeverityLevel.BLOCKER)
    public void retrievePostCommentsTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + postId + "/comments")
                .then()
                .log().all()
                .extract().response();
        //Find the comment with the specified id
        Map<String, Object> comment = response.jsonPath().getMap("find { it.id == " + commentId + " }");
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(comment.get("post_id"), postId);
        Assert.assertEquals(comment.get("name"), name);
        Assert.assertEquals(comment.get("email"), email);
        Assert.assertEquals(comment.get("body"), body);
    }

    @Test(priority = 2, description = "Retrieve post comments with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void retrievePostCommentsWithInvalidTokenTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .when()
                .get(endpoint + postId + "/comments")
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 3, description = "Retrieve post comments without post id")
    @Severity(SeverityLevel.NORMAL)
    public void retrievePostCommentsWithoutPostIdTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + "comments")
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }
}
