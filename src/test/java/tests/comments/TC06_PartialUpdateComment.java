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
@Feature("Partial Update Comment")
public class TC06_PartialUpdateComment extends TestBase {

    //Variables
    String endpoint = "/comments/";
    Comments comment;
    String name = commentData.getJsonData("comments[0].name");
    String email = commentData.getJsonData("comments[0].email");
    String body = commentData.getJsonData("comments[2].body");
    File schema = new File("src/test/resources/schemas/commentSchema.json");

    //Methods
    @Test(priority = 1, description = "Partial Update Comment")
    @Severity(SeverityLevel.BLOCKER)
    public void partialUpdateCommentTC() throws JsonProcessingException {
        // Comment data
        comment = new Comments()
                .setBody(body);
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .patch(endpoint + commentId)
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

    @Test(priority = 2, description = "Partial Update Comment with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void partialUpdateCommentWithInvalidTokenTC() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .body(mapper.writeValueAsString(comment))
                .when()
                .patch(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 2, description = "Partial Update Comment without token")
    @Severity(SeverityLevel.CRITICAL)
    public void partialUpdateCommentWithoutTokenTC() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json")))
                .body(mapper.writeValueAsString(comment))
                .when()
                .patch(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Partial Update Comment with non-existing ID")
    @Severity(SeverityLevel.NORMAL)
    public void partialUpdateCommentWithNonExistingIdTC() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .patch(endpoint + invalidId)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Partial Update Comment without ID")
    @Severity(SeverityLevel.NORMAL)
    public void partialUpdateCommentWithoutIdTC() throws JsonProcessingException {
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .patch(endpoint)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
    }

    @Test(priority = 4, description = "Partial Update Comment with empty name")
    @Severity(SeverityLevel.CRITICAL)
    public void partialUpdateCommentWithEmptyNameTC() throws JsonProcessingException {
        // Comment data
        comment = new Comments().setName("");
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .patch(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 4, description = "Partial Update Comment with empty email")
    @Severity(SeverityLevel.CRITICAL)
    public void partialUpdateCommentWithEmptyEmailTC() throws JsonProcessingException {
        // Comment data
        comment = new Comments().setEmail("");
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .patch(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank, is invalid");
    }

    @Test(priority = 4, description = "Partial Update Comment with empty comment body")
    @Severity(SeverityLevel.CRITICAL)
    public void partialUpdateCommentWithEmptyBodyTC() throws JsonProcessingException {
        // Comment data
        comment = new Comments().setBody("");
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .patch(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "can't be blank");
    }

    @Test(priority = 5, description = "Partial Update Comment with invalid email format")
    @Severity(SeverityLevel.CRITICAL)
    public void partialUpdateCommentWithInvalidEmailTC() throws JsonProcessingException {
        // Comment data
        comment = new Comments().setEmail("user123.com");
        // Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .body(mapper.writeValueAsString(comment))
                .when()
                .patch(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 422);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("[0].message"), "is invalid");
    }
}
