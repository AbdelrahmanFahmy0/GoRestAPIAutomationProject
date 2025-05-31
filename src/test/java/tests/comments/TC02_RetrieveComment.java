package tests.comments;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import tests.base.TestBase;

import java.io.File;
import java.util.Map;

import static builders.SpecBuilder.createRequestSpecification;
import static io.restassured.RestAssured.given;

@Epic("Comments")
@Feature("Retrieve Comment")
public class TC02_RetrieveComment extends TestBase {

    //Variables
    String endpoint = "/comments/";
    String name = commentData.getJsonData("comments[0].name");
    String email = commentData.getJsonData("comments[0].email");
    String body = commentData.getJsonData("comments[0].body");
    String updatedBody = commentData.getJsonData("comments[1].body");
    String partialUpdatedBody = commentData.getJsonData("comments[2].body");
    File schema = new File("src/test/resources/schemas/commentSchema.json");

    //Methods
    @Test(priority = 1, description = "Retrieve a comment")
    @Severity(SeverityLevel.BLOCKER)
    public void retrieveCommentTC() {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + commentId)
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

    @Test(priority = 2, description = "Retrieve a comment with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveCommentWithInvalidTokenTC() {
        // Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .when()
                .get(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 2, description = "Retrieve a comment without token")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveCommentWithoutTokenTC() {
        // Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json")))
                .when()
                .get(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Retrieve a comment with non-existing ID")
    @Severity(SeverityLevel.NORMAL)
    public void retrieveCommentWithNonExistingIdTC() {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + invalidId)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 4, description = "Retrieve a comment after update")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveCommentAfterUpdateTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + commentId)
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
        Assert.assertEquals(response.jsonPath().getString("body"), updatedBody);
    }

    @Test(priority = 5, description = "Retrieve a comment after partial update")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveCommentAfterPartialUpdateTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + commentId)
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
        Assert.assertEquals(response.jsonPath().getString("body"), partialUpdatedBody);
    }

    @Test(priority = 6, description = "Retrieve a comment after deletion")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveCommentAfterDeletionTC() {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }
}
