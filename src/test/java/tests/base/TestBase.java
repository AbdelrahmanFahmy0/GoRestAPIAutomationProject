package tests.base;

import io.qameta.allure.internal.shadowed.jackson.annotation.JsonInclude;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import listeners.TestNGListeners;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import utils.JsonUtils;

import java.util.Map;

@Listeners(TestNGListeners.class)
public class TestBase {

    //Variables
    public static int userId;
    public static String email;
    String token = new JsonUtils("authData").getJsonData("token");
    public String invalidToken = "2ec864f0d3496bd42246060ea433bcb29075f0f6fa6320b19047a";
    public int invalidId = 1000000;
    public static int postId;
    public static int commentId;
    public static int todoId;

    //Objects
    public ObjectMapper mapper = new ObjectMapper();
    public Map<String, String> headers = Map.of("content-type", "application/json", "Authorization", "Bearer " + token);
    public JsonUtils userData = new JsonUtils("usersData");
    public JsonUtils postData = new JsonUtils("postsData");
    public JsonUtils commentData = new JsonUtils("commentsData");
    public JsonUtils todoData = new JsonUtils("todosData");

    //Methods
    @BeforeTest
    public void setBaseURL() {
        // Set up the base URI for the API
        RestAssured.baseURI = "https://gorest.co.in/public/v2";
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
