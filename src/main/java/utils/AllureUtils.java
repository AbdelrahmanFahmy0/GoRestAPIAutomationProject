package utils;

import io.qameta.allure.restassured.AllureRestAssured;

public class AllureUtils {

    //Setting the request and response attachment names
    public static AllureRestAssured allureFilter() {
        return new AllureRestAssured()
                .setRequestAttachmentName("Request")
                .setResponseAttachmentName("Response");
    }
}
