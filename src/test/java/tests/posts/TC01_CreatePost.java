package tests.posts;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojo.Posts;
import tests.base.TestBase;

import java.io.File;
import java.util.Map;

import static builders.SpecBuilder.createRequestSpecification;
import static io.restassured.RestAssured.given;

@Epic("Posts")
@Feature("Create Post")
public class TC01_CreatePost extends TestBase {

    //Variables
    String endpoint = "/users/";
    Posts post;
    String title = postData.getJsonData("posts[0].title");
    String body = postData.getJsonData("posts[0].body");
    File schema = new File("src/test/resources/schemas/postSchema.json");

    //Methods
    @Test(priority = 1, description = "Create a post with valid data")
    @Severity(SeverityLevel.BLOCKER)
    public void createPostWithValidDataTC() throws JsonProcessingException {
        //user data
        post = new Posts()
                .setTitle(title)
                .setBody(body);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(post))
                .when()
                .post(endpoint + userId + "/posts")
                .then()
                .log().all()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schema))
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 201);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getInt("user_id"), userId);
        Assert.assertEquals(response.jsonPath().getString("title"), title);
        Assert.assertEquals(response.jsonPath().getString("body"), body);
        //Get postID
        postId = response.jsonPath().getInt("id");
    }

    @Test(priority = 2, description = "Create a post with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void createPostWithInvalidTokenTC() throws JsonProcessingException {
        //Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .body(mapper.writeValueAsString(post))
                .when()
                .post(endpoint + userId + "/posts")
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 2, description = "Create a post without token")
    @Severity(SeverityLevel.CRITICAL)
    public void createPostWithoutTokenTC() throws JsonProcessingException {
        //Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json")))
                .body(mapper.writeValueAsString(post))
                .when()
                .post(endpoint + userId + "/posts")
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Authentication failed");
    }

    @Test(priority = 3, description = "Create a post without body")
    @Severity(SeverityLevel.CRITICAL)
    public void createPostWithoutBodyTC() throws JsonProcessingException {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .post(endpoint + userId + "/posts")
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
        Assert.assertEquals(response.jsonPath().getString("[1].message"), "can't be blank");
    }

    @Test(priority = 3, description = "Create a post without userId")
    @Severity(SeverityLevel.NORMAL)
    public void createPostWithoutUserIdTC() throws JsonProcessingException {
        //user data
        post.setTitle(title)
                .setBody(body);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(post))
                .when()
                .post(endpoint + "posts")
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
    }

    @Test(priority = 3, description = "Create a post without title")
    @Severity(SeverityLevel.CRITICAL)
    public void createPostWithoutTitleTC() throws JsonProcessingException {
        //user data
        post.setTitle("")
                .setBody(body);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(post))
                .when()
                .post(endpoint + userId + "/posts")
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 3, description = "Create a post without post's body")
    @Severity(SeverityLevel.CRITICAL)
    public void createPostWithoutPostBodyTC() throws JsonProcessingException {
        //user data
        post.setTitle(title)
                .setBody("");
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(post))
                .when()
                .post(endpoint + userId + "/posts")
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Create a post with non-existing userId")
    @Severity(SeverityLevel.NORMAL)
    public void createPostWithNonExistingUserIdTC() throws JsonProcessingException {
        //user data
        post.setTitle(title)
                .setBody(body);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(post))
                .when()
                .post(endpoint + invalidId + "/posts")
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "must exist");
    }
}
