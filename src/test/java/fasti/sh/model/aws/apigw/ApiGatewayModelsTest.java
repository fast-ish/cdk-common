package fasti.sh.model.aws.apigw;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.apigw.usageplan.QuotaConf;
import fasti.sh.model.aws.apigw.usageplan.ThrottleConf;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.apigateway.Period;

/**
 * Tests for API Gateway model records.
 */
class ApiGatewayModelsTest {

  @Test
  void testThrottleConfBasic() {
    var throttle = new ThrottleConf(100.0, 200);

    assertEquals(100.0, throttle.rateLimit());
    assertEquals(200, throttle.burstLimit());
    assertRecordToString(throttle);
  }

  @Test
  void testThrottleConfHighLimit() {
    var throttle = new ThrottleConf(1000.0, 2000);

    assertEquals(1000.0, throttle.rateLimit());
    assertEquals(2000, throttle.burstLimit());
  }

  @Test
  void testThrottleConfLowLimit() {
    var throttle = new ThrottleConf(10.0, 20);

    assertEquals(10.0, throttle.rateLimit());
    assertEquals(20, throttle.burstLimit());
  }

  @Test
  void testThrottleConfEquality() {
    var throttle1 = new ThrottleConf(50.0, 100);
    var throttle2 = new ThrottleConf(50.0, 100);

    assertRecordEquality(throttle1, throttle2);
  }

  @Test
  void testQuotaConfDaily() {
    var quota = new QuotaConf(true, 10000, Period.DAY);

    assertTrue(quota.enabled());
    assertEquals(10000, quota.limit());
    assertEquals(Period.DAY, quota.period());
    assertRecordToString(quota);
  }

  @Test
  void testQuotaConfWeekly() {
    var quota = new QuotaConf(true, 50000, Period.WEEK);

    assertEquals(50000, quota.limit());
    assertEquals(Period.WEEK, quota.period());
  }

  @Test
  void testQuotaConfMonthly() {
    var quota = new QuotaConf(true, 200000, Period.MONTH);

    assertEquals(200000, quota.limit());
    assertEquals(Period.MONTH, quota.period());
  }

  @Test
  void testQuotaConfDisabled() {
    var quota = new QuotaConf(false, null, null);

    assertFalse(quota.enabled());
    assertNull(quota.limit());
    assertNull(quota.period());
  }

  @Test
  void testStageOptionsBasic() {
    var stage = new StageOptions(
      "prod",
      "Production stage",
      "INFO",
      Map.of(),
      false,
      false,
      true,
      false,
      100,
      50);

    assertEquals("prod", stage.stageName());
    assertEquals("Production stage", stage.description());
    assertEquals("INFO", stage.loggingLevel());
    assertFalse(stage.cachingEnabled());
    assertFalse(stage.dataTraceEnabled());
    assertTrue(stage.metricsEnabled());
    assertFalse(stage.tracingEnabled());
    assertEquals(100, stage.throttlingBurstLimit());
    assertEquals(50, stage.throttlingRateLimit());
    assertRecordToString(stage);
  }

  @Test
  void testStageOptionsWithCaching() {
    var stage = new StageOptions(
      "dev",
      "Development stage",
      "DEBUG",
      Map.of("key1", "value1"),
      true,
      true,
      true,
      true,
      200,
      100);

    assertTrue(stage.cachingEnabled());
    assertTrue(stage.dataTraceEnabled());
    assertTrue(stage.tracingEnabled());
    assertEquals(1, stage.variables().size());
  }

  @Test
  void testStageOptionsWithVariables() {
    var stage = new StageOptions(
      "test",
      "Test stage",
      "ERROR",
      Map.of("env", "test", "version", "1.0"),
      false,
      false,
      false,
      false,
      50,
      25);

    assertEquals(2, stage.variables().size());
    assertEquals("test", stage.variables().get("env"));
    assertEquals("1.0", stage.variables().get("version"));
  }

  @Test
  void testStageOptionsLoggingLevels() {
    var infoStage = new StageOptions(
      "stage1", "Stage 1", "INFO", Map.of(), false, false, false, false, 100, 50);
    var errorStage = new StageOptions(
      "stage2", "Stage 2", "ERROR", Map.of(), false, false, false, false, 100, 50);

    assertEquals("INFO", infoStage.loggingLevel());
    assertEquals("ERROR", errorStage.loggingLevel());
  }

  @Test
  void testRouteBasic() {
    var route = new Route(List.of("GET"), "/users");

    assertEquals(1, route.methods().size());
    assertEquals("GET", route.methods().get(0));
    assertEquals("/users", route.path());
    assertRecordToString(route);
  }

  @Test
  void testRouteMultipleMethods() {
    var route = new Route(List.of("GET", "POST", "PUT"), "/api/items");

    assertEquals(3, route.methods().size());
    assertTrue(route.methods().contains("GET"));
    assertTrue(route.methods().contains("POST"));
    assertTrue(route.methods().contains("PUT"));
    assertEquals("/api/items", route.path());
  }

  @Test
  void testRouteDelete() {
    var route = new Route(List.of("DELETE"), "/api/items/{id}");

    assertEquals(1, route.methods().size());
    assertEquals("DELETE", route.methods().get(0));
    assertEquals("/api/items/{id}", route.path());
  }

  @Test
  void testRouteOptions() {
    var route = new Route(List.of("OPTIONS"), "/api/*");

    assertEquals("OPTIONS", route.methods().get(0));
    assertEquals("/api/*", route.path());
  }

  @Test
  void testVpcLinkBasic() {
    var vpcLink = new VpcLink("my-vpc-link");

    assertEquals("my-vpc-link", vpcLink.name());
    assertRecordToString(vpcLink);
  }

  @Test
  void testVpcLinkProduction() {
    var vpcLink = new VpcLink("prod-vpc-link");

    assertEquals("prod-vpc-link", vpcLink.name());
  }

  @Test
  void testVpcLinkEquality() {
    var link1 = new VpcLink("link1");
    var link2 = new VpcLink("link1");

    assertRecordEquality(link1, link2);
  }

  @Test
  void testCorsOptionsBasic() {
    var cors = new CorsOptions(
      List.of("https://example.com", "https://app.example.com"),
      List.of("Content-Type", "Authorization", "X-Api-Key"),
      List.of("GET", "POST", "OPTIONS"));

    assertEquals(2, cors.allowOrigins().size());
    assertTrue(cors.allowOrigins().contains("https://example.com"));
    assertEquals(3, cors.allowHeaders().size());
    assertTrue(cors.allowHeaders().contains("Authorization"));
    assertEquals(3, cors.allowMethods().size());
    assertTrue(cors.allowMethods().contains("POST"));
    assertRecordToString(cors);
  }

  @Test
  void testCorsOptionsAllOrigins() {
    var cors = new CorsOptions(
      List.of("*"),
      List.of("*"),
      List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    assertEquals(1, cors.allowOrigins().size());
    assertEquals("*", cors.allowOrigins().get(0));
    assertEquals(5, cors.allowMethods().size());
  }

  @Test
  void testCorsOptionsRestrictive() {
    var cors = new CorsOptions(
      List.of("https://secure.example.com"),
      List.of("Content-Type"),
      List.of("GET"));

    assertEquals(1, cors.allowOrigins().size());
    assertEquals(1, cors.allowHeaders().size());
    assertEquals(1, cors.allowMethods().size());
  }

  @Test
  void testResponseModelBasic() {
    var model = new ResponseModel(
      "UserResponse",
      "User object response model",
      "application/json");

    assertEquals("UserResponse", model.modelName());
    assertEquals("User object response model", model.description());
    assertEquals("application/json", model.contentType());
    assertRecordToString(model);
  }

  @Test
  void testResponseModelError() {
    var model = new ResponseModel(
      "ErrorResponse",
      "Standard error response",
      "application/json");

    assertEquals("ErrorResponse", model.modelName());
    assertEquals("Standard error response", model.description());
  }

  @Test
  void testResponseModelXml() {
    var model = new ResponseModel(
      "XmlResponse",
      "XML format response",
      "application/xml");

    assertEquals("application/xml", model.contentType());
  }

  @Test
  void testMethodResponseSuccess() {
    var responseModel = new ResponseModel(
      "SuccessModel",
      "Success response",
      "application/json");

    var methodResponse = new MethodResponse(
      "200",
      Map.of("application/json", responseModel),
      Map.of("method.response.header.Access-Control-Allow-Origin", true));

    assertEquals("200", methodResponse.statusCode());
    assertEquals(1, methodResponse.responseModels().size());
    assertTrue(methodResponse.responseModels().containsKey("application/json"));
    assertEquals("SuccessModel", methodResponse.responseModels().get("application/json").modelName());
    assertEquals(1, methodResponse.responseParameters().size());
    assertTrue(methodResponse.responseParameters().get("method.response.header.Access-Control-Allow-Origin"));
    assertRecordToString(methodResponse);
  }

  @Test
  void testMethodResponseError() {
    var errorModel = new ResponseModel(
      "ErrorModel",
      "Error response",
      "application/json");

    var methodResponse = new MethodResponse(
      "400",
      Map.of("application/json", errorModel),
      Map.of());

    assertEquals("400", methodResponse.statusCode());
    assertTrue(methodResponse.responseParameters().isEmpty());
  }

  @Test
  void testMethodResponseMultipleModels() {
    var jsonModel = new ResponseModel("JsonModel", "JSON response", "application/json");
    var xmlModel = new ResponseModel("XmlModel", "XML response", "application/xml");

    var methodResponse = new MethodResponse(
      "200",
      Map
        .of(
          "application/json",
          jsonModel,
          "application/xml",
          xmlModel),
      Map
        .of(
          "method.response.header.Content-Type",
          true,
          "method.response.header.X-Request-Id",
          false));

    assertEquals(2, methodResponse.responseModels().size());
    assertEquals(2, methodResponse.responseParameters().size());
    assertTrue(methodResponse.responseParameters().get("method.response.header.Content-Type"));
    assertFalse(methodResponse.responseParameters().get("method.response.header.X-Request-Id"));
  }

  @Test
  void testMethodResponseCreated() {
    var model = new ResponseModel("CreatedModel", "Created response", "application/json");

    var methodResponse = new MethodResponse(
      "201",
      Map.of("application/json", model),
      Map.of("method.response.header.Location", true));

    assertEquals("201", methodResponse.statusCode());
    assertTrue(methodResponse.responseParameters().get("method.response.header.Location"));
  }

  @Test
  void testMethodResponseNoContent() {
    var methodResponse = new MethodResponse(
      "204",
      Map.of(),
      Map.of());

    assertEquals("204", methodResponse.statusCode());
    assertTrue(methodResponse.responseModels().isEmpty());
    assertTrue(methodResponse.responseParameters().isEmpty());
  }
}
