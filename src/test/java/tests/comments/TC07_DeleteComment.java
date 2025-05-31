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
@Feature("Delete Comment")
public class TC07_DeleteComment extends TestBase {

    //Variables
    String endpoint = "/comments/";

    //Methods
    @Test(priority = 1, description = "Delete a comment")
    @Severity(SeverityLevel.BLOCKER)
    public void deleteCommentTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .delete(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 204);
        Assert.assertTrue(response.getTime() < 3000);
    }

    @Test(priority = 2, description = "Delete a comment with invalid token")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteCommentWithInvalidTokenTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json", "Authorization", "Bearer " + invalidToken)))
                .when()
                .delete(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 401);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid token");
    }

    @Test(priority = 2, description = "Delete a comment without token")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteCommentWithoutTokenTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(Map.of("content-type", "application/json")))
                .when()
                .delete(endpoint + commentId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Delete a comment with non-existing ID")
    @Severity(SeverityLevel.NORMAL)
    public void deleteCommentWithNonExistingIdTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .delete(endpoint + invalidId)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");
    }

    @Test(priority = 3, description = "Delete a comment without ID")
    @Severity(SeverityLevel.NORMAL)
    public void deleteCommentWithoutIdTC() {
        //Request
        Response response = given()
                .spec(createRequestSpecification(headers))
                .when()
                .delete(endpoint)
                .then()
                .log().all()
                .extract().response();
        //Assertions
        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertTrue(response.getTime() < 3000);
    }
}
