package tests.posts;

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

@Epic("Posts")
@Feature("Retrieve User Posts")
public class TC04_RetrieveUserPosts extends TestBase {

    //Variables
    String endpoint = "/users/";
    String title = postData.getJsonData("posts[0].title");
    String body = postData.getJsonData("posts[0].body");

    //Methods
    @Test(priority = 1, description = "Retrieve user posts")
    @Severity(SeverityLevel.BLOCKER)
    public void retrieveUserPostsTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + userId + "/posts")
                .then()
                .log().all()
                .extract().response();
        //Find the post with the specified id
        Map<String, Object> post = response.jsonPath().getMap("find { it.id == " + postId + " }");
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(post.get("user_id"), userId);
        Assert.assertEquals(post.get("title"), title);
        Assert.assertEquals(post.get("body"), body);
    }

    @Test(priority = 2, description = "Retrieve user posts with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveUserPostsWithInvalidTokenTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .when()
                .get(endpoint + userId + "/posts")
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 3, description = "Retrieve user posts without user ID")
    @Severity(SeverityLevel.NORMAL)
    public void retrieveUserPostsWithoutUserIdTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + "posts")
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }
}
