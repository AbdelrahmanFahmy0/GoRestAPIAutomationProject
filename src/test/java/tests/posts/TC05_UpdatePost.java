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
@Feature("Update Post")
public class TC05_UpdatePost extends TestBase {

    //Variables
    String endpoint = "/posts/";
    Posts post;
    String title = postData.getJsonData("posts[0].title");
    String body = postData.getJsonData("posts[1].body");
    File schema = new File("src/test/resources/schemas/postSchema.json");

    //Methods
    @Test(priority = 1, description = "Update a post")
    @Severity(SeverityLevel.BLOCKER)
    public void updatePostTC() throws JsonProcessingException {
        //user data
        post = new Posts()
                .setTitle(title)
                .setBody(body);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(post))
                .when()
                .put(endpoint + postId)
                .then()
                .log().all()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schema))
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getInt("user_id"), userId);
        Assert.assertEquals(response.jsonPath().getString("title"), title);
        Assert.assertEquals(response.jsonPath().getString("body"), body);
    }

    @Test(priority = 2, description = "Update a post with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void updatePostWithInvalidTokenTC() throws JsonProcessingException {
        //Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .body(mapper.writeValueAsString(post))
                .when()
                .put(endpoint + postId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 2, description = "Update a post without token")
    @Severity(SeverityLevel.CRITICAL)
    public void updatePostWithoutTokenTC() throws JsonProcessingException {
        //Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json")))
                .body(mapper.writeValueAsString(post))
                .when()
                .put(endpoint + postId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Update a post with non-existing ID")
    @Severity(SeverityLevel.NORMAL)
    public void updatePostWithNonExistingIdTC() throws JsonProcessingException {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(post))
                .when()
                .put(endpoint + invalidId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Update a post without ID")
    @Severity(SeverityLevel.NORMAL)
    public void updatePostWithoutIdTC() throws JsonProcessingException {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(post))
                .when()
                .put(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
    }

    @Test(priority = 4, description = "Update a post with empty body")
    @Severity(SeverityLevel.CRITICAL)
    public void updatePostWithEmptyBodyTC() throws JsonProcessingException {
        //user data
        post = new Posts()
                .setTitle("")
                .setBody("");
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(post))
                .when()
                .put(endpoint + postId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
        Assert.assertEquals(response.jsonPath().getString("[1].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Update a post with empty title")
    @Severity(SeverityLevel.CRITICAL)
    public void updatePostWithEmptyTitleTC() throws JsonProcessingException {
        //user data
        post = new Posts()
                .setTitle("")
                .setBody(body);
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(post))
                .when()
                .put(endpoint + postId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Update a post with empty body")
    @Severity(SeverityLevel.CRITICAL)
    public void updatePostWithEmptyPostBodyTC() throws JsonProcessingException {
        //user data
        post = new Posts()
                .setTitle(title)
                .setBody("");
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(post))
                .when()
                .put(endpoint + postId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }
}
