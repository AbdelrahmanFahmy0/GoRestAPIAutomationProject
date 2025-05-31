package builders;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static utils.AllureUtils.allureFilter;

public class SpecBuilder {

    // This class is used to build RequestSpecifications for API tests.
    public static RequestSpecification createRequestSpecification(Map headers) {
        return new RequestSpecBuilder()
                .addFilter(allureFilter())
                .addFilter(new RequestLoggingFilter())
                .addHeaders(headers)
                .build();
    }
}
