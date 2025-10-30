package fasti.sh.test;

import fasti.sh.model.main.Common;
import java.util.Map;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

/**
 * Utility class for testing AWS CDK constructs with minimal boilerplate. Provides reusable Stack and Common instances for construct
 * testing.
 */
public final class CdkTestUtil {

  private CdkTestUtil() {}

  /**
   * Creates a test CDK App for construct testing.
   *
   * @return new CDK App instance
   */
  public static App createTestApp() {
    return new App();
  }

  /**
   * Creates a test CDK Stack for construct testing.
   *
   * @param app
   *          the parent App
   * @param stackId
   *          the stack identifier
   * @return new Stack instance
   */
  public static Stack createTestStack(App app, String stackId) {
    var env = Environment
      .builder()
      .account("123456789012")
      .region("us-east-1")
      .build();

    var stack = new Stack(
      app,
      stackId,
      StackProps
        .builder()
        .stackName(stackId)
        .env(env)
        .build());

    // Add CDK context for Template.parse()
    addTestContext(stack);

    return stack;
  }

  /**
   * Adds test context values required by Template.parse().
   *
   * @param stack
   *          the stack to add context to
   */
  private static void addTestContext(Stack stack) {
    stack.getNode().setContext("home", "/");
    stack.getNode().setContext("host:id", "test");
    stack.getNode().setContext("host:organization", "test-org");
    stack.getNode().setContext("host:account", "123456789012");
    stack.getNode().setContext("host:region", "us-east-1");
    stack.getNode().setContext("host:name", "test-deployment");
    stack.getNode().setContext("host:alias", "test");
    stack.getNode().setContext("host:environment", "production");
    stack.getNode().setContext("host:version", "v1");
    stack.getNode().setContext("host:domain", "example.com");
    stack.getNode().setContext("hosted:id", "test");
    stack.getNode().setContext("hosted:organization", "test-org");
    stack.getNode().setContext("hosted:account", "123456789012");
    stack.getNode().setContext("hosted:region", "us-east-1");
    stack.getNode().setContext("hosted:name", "test-deployment");
    stack.getNode().setContext("hosted:alias", "test");
    stack.getNode().setContext("hosted:environment", "production");
    stack.getNode().setContext("hosted:version", "v1");
    stack.getNode().setContext("hosted:domain", "example.com");
    stack.getNode().setContext("hosted:synthesizer:name", "test-synthesizer");
  }

  /**
   * Creates a test Stack with default ID.
   *
   * @param app
   *          the parent App
   * @return new Stack instance
   */
  public static Stack createTestStack(App app) {
    return createTestStack(app, "test-stack");
  }

  /**
   * Creates a test Common configuration for construct testing.
   *
   * @return Common instance with test values
   */
  public static Common createTestCommon() {
    return Common
      .builder()
      .id("test")
      .account("123456789012")
      .region("us-east-1")
      .organization("test-org")
      .name("test-deployment")
      .alias("test")
      .environment("production")
      .version("1.0.0")
      .domain("example.com")
      .tags(Map.of("Environment", "test", "ManagedBy", "cdk"))
      .build();
  }

  /**
   * Creates a test scope (Stack) in a new App for simple construct testing.
   *
   * @return Stack that can be used as a construct scope
   */
  public static Stack createTestScope() {
    return createTestStack(createTestApp());
  }

  /**
   * Synthesizes a Stack to validate construct creation succeeded. This exercises CDK's construct tree validation.
   *
   * @param stack
   *          the stack to synthesize
   */
  public static void synthesizeStack(Stack stack) {
    var app = (App) stack.getNode().getScope();
    app.synth();
  }

  /**
   * Creates a construct scope and common config for testing.
   *
   * @return test context with scope and common
   */
  public static TestContext createTestContext() {
    return new TestContext(createTestScope(), createTestCommon());
  }

  /**
   * Test context containing a CDK scope and Common configuration.
   */
  public record TestContext(
    Construct scope,
    Common common
  ) {}
}
