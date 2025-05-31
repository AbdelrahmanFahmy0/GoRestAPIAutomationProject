package tests.posts;

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

@Epic("Posts")
@Feature("Retrieve Post")
public class TC02_RetrievePost extends TestBase {

    //Variables
    String endpoint = "/posts/";
    String title = postData.getJsonData("posts[0].title");
    String body = postData.getJsonData("posts[0].body");
    String updatedBody = postData.getJsonData("posts[1].body");
    String partialUpdatedBody = postData.getJsonData("posts[2].body");
    File schema = new File("src/test/resources/schemas/postSchema.json");

    //Methods
    @Test(priority = 1, description = "Retrieve a post")
    @Severity(SeverityLevel.BLOCKER)
    public void retrievePostTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + postId)
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

    @Test(priority = 2, description = "Retrieve a post without token")
    @Severity(SeverityLevel.CRITICAL)
    public void retrievePostWithoutTokenTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json")))
                .when()
                .get(endpoint + postId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 2, description = "Retrieve a post with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void retrievePostWithInvalidTokenTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .when()
                .get(endpoint + postId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 3, description = "Retrieve a post with non-existing ID")
    @Severity(SeverityLevel.NORMAL)
    public void retrievePostWithNonExistingIdTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + invalidId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 4, description = "Retrieve a post after update")
    @Severity(SeverityLevel.CRITICAL)
    public void retrievePostAfterUpdateTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + postId)
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
        Assert.assertEquals(response.jsonPath().getString("body"), updatedBody);
    }

    @Test(priority = 5, description = "Retrieve a post after partial update")
    @Severity(SeverityLevel.CRITICAL)
    public void retrievePostAfterPartialUpdateTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + postId)
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
        Assert.assertEquals(response.jsonPath().getString("body"), partialUpdatedBody);
    }

    @Test(priority = 6, description = "Retrieve a post after deletion")
    @Severity(SeverityLevel.CRITICAL)
    public void retrievePostAfterDeleteTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .get(endpoint + postId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }
}
