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
@Feature("Retrieve All Posts")
public class TC03_RetrieveAllPosts extends TestBase {

    //Variables
    String endpoint = "/posts";
    String title = postData.getJsonData("posts[0].title");
    String body = postData.getJsonData("posts[0].body");

    //Methods
    @Test(priority = 1, description = "Retrieve all posts")
    @Severity(SeverityLevel.BLOCKER)
    public void retrieveAllPostsTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint)
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

    @Test(priority = 2, description = "Retrieve all posts with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveAllPostsWithInvalidTokenTC() {
        //Request
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
