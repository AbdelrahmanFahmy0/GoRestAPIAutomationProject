package tests.comments;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojo.Comments;
import tests.base.TestBase;

import java.io.File;
import java.util.Map;

import static builders.SpecBuilder.createRequestSpecification;
import static io.restassured.RestAssured.given;

@Epic("Comments")
@Feature("Create Comment")
public class TC01_CreateComment extends TestBase {

    //Variables
    String endpoint = "/posts/";
    Comments comment;
    String name = commentData.getJsonData("comments[0].name");
    String email = commentData.getJsonData("comments[0].email");
    String body = commentData.getJsonData("comments[0].body");
    File schema = new File("src/test/resources/schemas/commentSchema.json");

    //Methods
    @Test(priority = 1, description = "Create a comment with valid data")
    @Severity(SeverityLevel.BLOCKER)
    public void createCommentWithValidDataTC() throws JsonProcessingException {
        // Comment data
        comment = new Comments()
                .setName(name)
                .setEmail(email)
                .setBody(body);

        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .post(endpoint + postId + "/comments")
                .then()
                .log().all()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schema))
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 201);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getInt("post_id"), postId);
        Assert.assertEquals(response.jsonPath().getString("name"), name);
        Assert.assertEquals(response.jsonPath().getString("email"), email);
        Assert.assertEquals(response.jsonPath().getString("body"), body);

        // Get comment ID
        commentId = response.jsonPath().getInt("id");
    }

    @Test(priority = 2, description = "Create a comment with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void createCommentWithInvalidTokenTC() throws JsonProcessingException {
        // Request with invalid token
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .body(mapper.writeValueAsString(comment))
                .when()
                .post(endpoint + postId + "/comments")
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 2, description = "Create a comment without token")
    @Severity(SeverityLevel.CRITICAL)
    public void createCommentWithoutTokenTC() throws JsonProcessingException {
        // Request without token
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json")))
                .body(mapper.writeValueAsString(comment))
                .when()
                .post(endpoint + postId + "/comments")
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Authentication failed");
    }

    @Test(priority = 3, description = "Create a comment with non-existing post ID")
    @Severity(SeverityLevel.NORMAL)
    public void createCommentWithNonExistingPostIdTC() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .post(endpoint + invalidId + "/comments")
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "must exist");
    }

    @Test(priority = 3, description = "Create a comment without post ID")
    @Severity(SeverityLevel.CRITICAL)
    public void createCommentWithoutPostIdTC() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .post(endpoint + "comments")
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
    }

    @Test(priority = 4, description = "Create a comment without body")
    @Severity(SeverityLevel.CRITICAL)
    public void createCommentWithoutBodyTC() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .post(endpoint + postId + "/comments")
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
        Assert.assertEquals(response.jsonPath().getString("[1].message"), "can't be blank, is invalid");
        Assert.assertEquals(response.jsonPath().getString("[2].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Create a comment with empty name")
    @Severity(SeverityLevel.CRITICAL)
    public void createCommentWithEmptyNameTC() throws JsonProcessingException {
        // Comment data with empty name
        comment.setName("")
                .setEmail(email)
                .setBody(body);

        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .post(endpoint + postId + "/comments")
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Create a comment with empty email")
    @Severity(SeverityLevel.CRITICAL)
    public void createCommentWithEmptyEmailTC() throws JsonProcessingException {
        // Comment data with empty email
        comment.setName(name)
                .setEmail("")
                .setBody(body);

        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .post(endpoint + postId + "/comments")
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank, is invalid");
    }

    @Test(priority = 4, description = "Create a comment with empty body")
    @Severity(SeverityLevel.CRITICAL)
    public void createCommentWithEmptyBodyTC() throws JsonProcessingException {
        // Comment data with empty body
        comment.setName(name)
                .setEmail(email)
                .setBody("");

        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .post(endpoint + postId + "/comments")
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 5, description = "Create a comment with invalid email format")
    @Severity(SeverityLevel.CRITICAL)
    public void createCommentWithInvalidEmailFormatTC() throws JsonProcessingException {
        // Comment data with invalid email format
        comment.setName(name)
                .setEmail("user123.com")
                .setBody(body);

        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .post(endpoint + postId + "/comments")
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "is invalid");
    }
}
