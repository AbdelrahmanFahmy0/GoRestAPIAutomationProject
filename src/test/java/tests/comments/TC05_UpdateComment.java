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
@Feature("Update Comment")
public class TC05_UpdateComment extends TestBase {

    //Variables
    String endpoint = "/comments/";
    Comments comment;
    String name = commentData.getJsonData("comments[0].name");
    String email = commentData.getJsonData("comments[0].email");
    String body = commentData.getJsonData("comments[1].body");
    File schema = new File("src/test/resources/schemas/commentSchema.json");

    //Methods
    @Test(priority = 1, description = "Update a comment")
    @Severity(SeverityLevel.BLOCKER)
    public void updateCommentTC() throws JsonProcessingException {
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
                .put(endpoint + commentId)
                .then()
                .log().all()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schema))
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getInt("post_id"), postId);
        Assert.assertEquals(response.jsonPath().getString("name"), name);
        Assert.assertEquals(response.jsonPath().getString("email"), email);
        Assert.assertEquals(response.jsonPath().getString("body"), body);
    }

    @Test(priority = 2, description = "Update a comment with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void updateCommentWithInvalidTokenTC() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .body(mapper.writeValueAsString(comment))
                .when()
                .put(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 2, description = "Update a comment without token")
    @Severity(SeverityLevel.CRITICAL)
    public void updateCommentWithoutTokenTC() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json")))
                .body(mapper.writeValueAsString(comment))
                .when()
                .put(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Update a comment with non-existing ID")
    @Severity(SeverityLevel.NORMAL)
    public void updateCommentWithNonExistingIdTC() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
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

    @Test(priority = 3, description = "Update a comment without ID")
    @Severity(SeverityLevel.NORMAL)
    public void updateCommentWithoutIdTC() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .put(endpoint)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
    }

    @Test(priority = 4, description = "Update a comment with empty body")
    @Severity(SeverityLevel.CRITICAL)
    public void updateCommentWithEmptyBodyTC() throws JsonProcessingException {
        // Comment data
        comment = new Comments()
                .setName("")
                .setEmail("")
                .setBody("");
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .put(endpoint + commentId)
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

    @Test(priority = 4, description = "Update a comment with empty name")
    @Severity(SeverityLevel.CRITICAL)
    public void updateCommentWithEmptyNameTC() throws JsonProcessingException {
        // Comment data
        comment = new Comments()
                .setName("")
                .setEmail(email)
                .setBody(body);
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .put(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Update a comment with empty email")
    @Severity(SeverityLevel.CRITICAL)
    public void updateCommentWithEmptyEmailTC() throws JsonProcessingException {
        // Comment data
        comment = new Comments()
                .setName(name)
                .setEmail("")
                .setBody(body);
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .put(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank, is invalid");
    }

    @Test(priority = 4, description = "Update a comment with empty comment body")
    @Severity(SeverityLevel.CRITICAL)
    public void updateCommentWithEmptyCommentBodyTC() throws JsonProcessingException {
        // Comment data
        comment = new Comments()
                .setName(name)
                .setEmail(email)
                .setBody("");
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .put(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 5, description = "Update a comment with invalid email")
    @Severity(SeverityLevel.CRITICAL)
    public void updateCommentWithInvalidEmailTC() throws JsonProcessingException {
        // Comment data
        comment = new Comments()
                .setName(name)
                .setEmail("user123.com")
                .setBody(body);
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .put(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "is invalid");
    }
}
