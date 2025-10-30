package fasti.sh.model.aws.fn;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.iam.IamRole;
import fasti.sh.model.aws.iam.Principal;
import fasti.sh.model.aws.iam.PrincipalType;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.lambda.Runtime;

/**
 * Tests for Lambda model records.
 */
class LambdaModelsTest {

  @Test
  void testLambdaLayerBasic() {
    var layer = new LambdaLayer(
      "my-layer",
      "layers/my-layer.zip",
      RemovalPolicy.DESTROY,
      List.of("python3.11", "python3.12"));

    assertEquals("my-layer", layer.name());
    assertEquals("layers/my-layer.zip", layer.asset());
    assertEquals(RemovalPolicy.DESTROY, layer.removalPolicy());
    assertEquals(2, layer.runtimes().size());
    assertRecordToString(layer);
  }

  @Test
  void testLambdaLayerJava() {
    var layer = new LambdaLayer(
      "java-utils",
      "layers/java-utils.zip",
      RemovalPolicy.RETAIN,
      List.of("java21", "java17"));

    assertEquals("java-utils", layer.name());
    assertEquals(RemovalPolicy.RETAIN, layer.removalPolicy());
    assertTrue(layer.runtimes().contains("java21"));
  }

  @Test
  void testLambdaLayerEquality() {
    var layer1 = new LambdaLayer("l1", "asset", RemovalPolicy.DESTROY, List.of("python3.11"));
    var layer2 = new LambdaLayer("l1", "asset", RemovalPolicy.DESTROY, List.of("python3.11"));

    assertRecordEquality(layer1, layer2);
  }

  @Test
  void testLambdaBasic() {
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("lambda.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "lambda-role",
      "Lambda execution role",
      principal,
      List.of("service-role/AWSLambdaBasicExecutionRole"),
      List.of(),
      Map.of());

    var lambda = new Lambda(
      "my-function",
      "My Lambda function",
      "target/function.zip",
      "com.example.Handler::handleRequest",
      "PRIVATE",
      30,
      512,
      Runtime.JAVA_21,
      role,
      List.of(),
      List.of(),
      Map.of());

    assertEquals("my-function", lambda.name());
    assertEquals("My Lambda function", lambda.description());
    assertEquals(30, lambda.timeout());
    assertEquals(512, lambda.memorySize());
    assertEquals(Runtime.JAVA_21, lambda.runtime());
    assertRecordToString(lambda);
  }

  @Test
  void testLambdaPython() {
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("lambda.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "python-lambda-role",
      "Python Lambda role",
      principal,
      List.of(),
      List.of(),
      Map.of());

    var layer = new LambdaLayer(
      "requests-layer",
      "layers/requests.zip",
      RemovalPolicy.DESTROY,
      List.of("python3.11"));

    var lambda = new Lambda(
      "python-function",
      "Python function",
      "target/lambda.zip",
      "index.handler",
      "PUBLIC",
      60,
      1024,
      Runtime.PYTHON_3_11,
      role,
      List.of(),
      List.of(layer),
      Map.of("LOG_LEVEL", "INFO"));

    assertEquals(Runtime.PYTHON_3_11, lambda.runtime());
    assertEquals(1, lambda.layers().size());
    assertEquals("requests-layer", lambda.layers().get(0).name());
    assertEquals(1, lambda.environment().size());
  }

  @Test
  void testLambdaWithInvokers() {
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("lambda.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "lambda-role",
      "Role",
      principal,
      List.of(),
      List.of(),
      Map.of());

    var apiGatewayPrincipal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("apigateway.amazonaws.com")
      .conditions(Map.of())
      .build();

    var lambda = new Lambda(
      "api-function",
      "API Lambda",
      "target/api.zip",
      "handler",
      "PRIVATE",
      15,
      256,
      Runtime.NODEJS_18_X,
      role,
      List.of(apiGatewayPrincipal),
      List.of(),
      Map.of());

    assertEquals(1, lambda.invokers().size());
    assertEquals("apigateway.amazonaws.com", lambda.invokers().get(0).value());
  }

  @Test
  void testLambdaHighMemory() {
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("lambda.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "lambda-role",
      "Role",
      principal,
      List.of(),
      List.of(),
      Map.of());

    var lambda = new Lambda(
      "heavy-function",
      "Memory-intensive function",
      "target/heavy.zip",
      "handler",
      "PRIVATE",
      900,
      10240,
      Runtime.JAVA_21,
      role,
      List.of(),
      List.of(),
      Map.of("HEAP_SIZE", "8G"));

    assertEquals(900, lambda.timeout());
    assertEquals(10240, lambda.memorySize());
  }

  @Test
  void testRequestValidatorBasic() {
    var validator = new RequestValidator("basic-validator", true, false);

    assertEquals("basic-validator", validator.name());
    assertTrue(validator.validateRequestParameters());
    assertFalse(validator.validateRequestBody());
    assertRecordToString(validator);
  }

  @Test
  void testRequestValidatorBodyOnly() {
    var validator = new RequestValidator("body-validator", false, true);

    assertFalse(validator.validateRequestParameters());
    assertTrue(validator.validateRequestBody());
  }

  @Test
  void testRequestValidatorAll() {
    var validator = new RequestValidator("full-validator", true, true);

    assertTrue(validator.validateRequestParameters());
    assertTrue(validator.validateRequestBody());
  }

  @Test
  void testRequestValidatorNone() {
    var validator = new RequestValidator("no-validation", false, false);

    assertFalse(validator.validateRequestParameters());
    assertFalse(validator.validateRequestBody());
  }

  @Test
  void testIntegrationOptionsBasic() {
    var options = new IntegrationOptions(
      "getUser",
      null,
      false,
      List.of(),
      List.of(),
      null,
      Map.of(),
      List.of());

    assertEquals("getUser", options.operationName());
    assertFalse(options.apiKeyRequired());
    assertTrue(options.authorizationScopes().isEmpty());
    assertRecordToString(options);
  }

  @Test
  void testIntegrationOptionsWithAuth() {
    var options = new IntegrationOptions(
      "createUser",
      null,
      true,
      List.of("read:users", "write:users"),
      List.of("UserModel"),
      "basic-validator",
      Map.of("method.request.header.Authorization", true),
      List.of());

    assertTrue(options.apiKeyRequired());
    assertEquals(2, options.authorizationScopes().size());
    assertEquals("UserModel", options.requestModels().get(0));
    assertEquals("basic-validator", options.requestValidator());
  }
}
