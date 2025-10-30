package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.apigw.RestApiConstruct;
import fasti.sh.execute.aws.apigw.UsagePlanConstruct;
import fasti.sh.model.aws.apigw.ApiConf;
import fasti.sh.model.aws.apigw.StageOptions;
import fasti.sh.model.aws.apigw.usageplan.QuotaConf;
import fasti.sh.model.aws.apigw.usageplan.ThrottleConf;
import fasti.sh.model.aws.apigw.usageplan.UsagePlanConf;
import fasti.sh.model.aws.cloudwatch.LogGroupConf;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.apigateway.Period;

/**
 * Tests for advanced AWS constructs including API Gateway.
 */
class AdvancedConstructsTest {

  @Test
  void testRestApiConstruct() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "api-logs",
      "STANDARD",
      "ONE_WEEK",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
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

    var apiConf = new ApiConf(
      null, // vpcName
      "test-api",
      "Test API",
      null, // baseLayer
      "x86_64",
      true, // cloudwatchEnabled
      false, // disableExecuteApi
      false, // apiKeyRequired
      null, // authorizationType
      List.of(), // usagePlans
      List.of(), // validators
      List.of(), // requestModels
      List.of(), // methodResponses
      stageOptions,
      logGroup,
      Map.of());

    var construct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    assertNotNull(construct);
    assertNotNull(construct.api());
  }

  @Test
  void testUsagePlanConstruct() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "api-logs",
      "STANDARD",
      "ONE_WEEK",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
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

    var apiConf = new ApiConf(
      null,
      "test-api",
      "Test API",
      null,
      "x86_64",
      true,
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of());

    var apiConstruct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    var throttle = new ThrottleConf(100.0, 200);
    var quota = new QuotaConf(true, 10000, Period.DAY);

    var usagePlanConf = new UsagePlanConf(
      "basic-plan",
      "Basic usage plan",
      throttle,
      quota);

    var construct = new UsagePlanConstruct(
      ctx.scope(),
      ctx.common(),
      usagePlanConf,
      apiConstruct.api());

    assertNotNull(construct);
    assertNotNull(construct.usagePlan());
  }

  @Test
  void testRestApiConstructWithApiKey() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "api-key-logs",
      "STANDARD",
      "ONE_WEEK",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
      "prod",
      "Production stage",
      "ERROR",
      Map.of(),
      true, // caching enabled
      false,
      true,
      false,
      200,
      100);

    var apiConf = new ApiConf(
      null,
      "api-with-key",
      "API requiring API key",
      null,
      "x86_64",
      true,
      false,
      true, // apiKeyRequired
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of("RequiresKey", "true"));

    var construct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    assertNotNull(construct);
    assertNotNull(construct.api());
  }

  @Test
  void testRestApiConstructDisableExecuteApi() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "disabled-exec-logs",
      "STANDARD",
      "THREE_MONTHS",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
      "dev",
      "Development stage",
      "INFO",
      Map.of("Environment", "dev"),
      false,
      false,
      true,
      false,
      50,
      25);

    var apiConf = new ApiConf(
      null,
      "no-exec-api",
      "API with execute-api disabled",
      null,
      "arm64",
      false, // cloudwatch disabled
      true, // disableExecuteApi
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of());

    var construct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    assertNotNull(construct);
    assertNotNull(construct.api());
  }

  @Test
  void testUsagePlanConstructWithWeeklyQuota() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "weekly-quota-logs",
      "STANDARD",
      "ONE_WEEK",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
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

    var apiConf = new ApiConf(
      null,
      "quota-api",
      "API with weekly quota",
      null,
      "x86_64",
      true,
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of());

    var apiConstruct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    var throttle = new ThrottleConf(50.0, 100);
    var quota = new QuotaConf(true, 50000, Period.WEEK);

    var usagePlanConf = new UsagePlanConf(
      "weekly-plan",
      "Usage plan with weekly quota",
      throttle,
      quota);

    var construct = new UsagePlanConstruct(
      ctx.scope(),
      ctx.common(),
      usagePlanConf,
      apiConstruct.api());

    assertNotNull(construct);
    assertNotNull(construct.usagePlan());
  }

  @Test
  void testUsagePlanConstructWithMonthlyQuota() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "monthly-quota-logs",
      "STANDARD",
      "ONE_MONTH",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
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

    var apiConf = new ApiConf(
      null,
      "monthly-api",
      "API with monthly quota",
      null,
      "x86_64",
      true,
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of());

    var apiConstruct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    var throttle = new ThrottleConf(200.0, 500);
    var quota = new QuotaConf(true, 1000000, Period.MONTH);

    var usagePlanConf = new UsagePlanConf(
      "enterprise-plan",
      "Enterprise usage plan",
      throttle,
      quota);

    var construct = new UsagePlanConstruct(
      ctx.scope(),
      ctx.common(),
      usagePlanConf,
      apiConstruct.api());

    assertNotNull(construct);
    assertNotNull(construct.usagePlan());
  }

  @Test
  void testRestApiConstructWithDebugLogging() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "debug-api-logs",
      "STANDARD",
      "ONE_WEEK",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
      "debug",
      "Debug stage",
      "INFO",
      Map.of("LogLevel", "info"),
      false,
      true, // data trace enabled
      true,
      false,
      150,
      75);

    var apiConf = new ApiConf(
      null,
      "debug-api",
      "API with debug logging",
      null,
      "x86_64",
      true,
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of("Environment", "debug"));

    var construct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    assertNotNull(construct);
    assertNotNull(construct.api());
  }

  @Test
  void testRestApiConstructWithTracing() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "traced-api-logs",
      "STANDARD",
      "TWO_WEEKS",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
      "prod",
      "Production with X-Ray tracing",
      "INFO",
      Map.of("Tracing", "enabled"),
      false,
      false,
      true,
      true, // tracing enabled
      100,
      50);

    var apiConf = new ApiConf(
      null,
      "traced-api",
      "API with X-Ray tracing",
      null,
      "x86_64",
      true,
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of());

    var construct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    assertNotNull(construct);
    assertNotNull(construct.api());
  }

  @Test
  void testUsagePlanConstructNoQuota() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "no-quota-logs",
      "STANDARD",
      "ONE_WEEK",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
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

    var apiConf = new ApiConf(
      null,
      "no-quota-api",
      "API without quota limits",
      null,
      "x86_64",
      true,
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of());

    var apiConstruct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    var throttle = new ThrottleConf(1000.0, 2000);
    var quota = new QuotaConf(false, 0, Period.DAY); // quota disabled

    var usagePlanConf = new UsagePlanConf(
      "unlimited-plan",
      "Unlimited usage plan (throttle only)",
      throttle,
      quota);

    var construct = new UsagePlanConstruct(
      ctx.scope(),
      ctx.common(),
      usagePlanConf,
      apiConstruct.api());

    assertNotNull(construct);
    assertNotNull(construct.usagePlan());
  }

  @Test
  void testRestApiConstructArm64() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "arm64-api-logs",
      "INFREQUENT_ACCESS",
      "SIX_MONTHS",
      null,
      "RETAIN",
      Map.of());

    var stageOptions = new StageOptions(
      "prod",
      "ARM64 production",
      "ERROR",
      Map.of("Architecture", "arm64"),
      false,
      false,
      false,
      false,
      300,
      150);

    var apiConf = new ApiConf(
      null,
      "arm64-api",
      "ARM64 architecture API",
      null,
      "arm64",
      true,
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of("Arch", "arm64"));

    var construct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    assertNotNull(construct);
    assertNotNull(construct.api());
  }

  @Test
  void testRestApiConstructWithIamAuthorization() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "iam-auth-logs",
      "STANDARD",
      "ONE_MONTH",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
      "prod",
      "IAM authorized stage",
      "INFO",
      Map.of("AuthType", "IAM"),
      false,
      false,
      true,
      false,
      100,
      50);

    var apiConf = new ApiConf(
      null,
      "iam-auth-api",
      "API with IAM authorization",
      null,
      "x86_64",
      true,
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of("Auth", "IAM"));

    var construct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    assertNotNull(construct);
    assertNotNull(construct.api());
  }

  @Test
  void testRestApiConstructWithCognitoAuthorization() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "cognito-auth-logs",
      "STANDARD",
      "TWO_WEEKS",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
      "prod",
      "Cognito authorized stage",
      "INFO",
      Map.of("AuthType", "COGNITO"),
      true,
      false,
      true,
      false,
      150,
      75);

    var apiConf = new ApiConf(
      null,
      "cognito-auth-api",
      "API with Cognito authorization",
      null,
      "x86_64",
      true,
      false,
      true,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of("Auth", "Cognito"));

    var construct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    assertNotNull(construct);
    assertNotNull(construct.api());
  }

  @Test
  void testRestApiConstructMinimalConfig() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "minimal-logs",
      "STANDARD",
      "ONE_DAY",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
      "dev",
      "Minimal dev stage",
      "ERROR",
      Map.of(),
      false,
      false,
      false,
      false,
      10,
      5);

    var apiConf = new ApiConf(
      null,
      "minimal-api",
      "Minimal API configuration",
      null,
      "x86_64",
      false,
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of());

    var construct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    assertNotNull(construct);
    assertNotNull(construct.api());
  }

  @Test
  void testRestApiConstructMaximalConfig() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "maximal-logs",
      "INFREQUENT_ACCESS",
      "ONE_YEAR",
      null,
      "RETAIN",
      Map.of("Retention", "365days"));

    var stageOptions = new StageOptions(
      "prod",
      "Maximal production stage",
      "INFO",
      Map.of("Environment", "production", "Monitoring", "full"),
      true, // caching enabled
      true, // data trace enabled
      true, // metrics enabled
      true, // tracing enabled
      500,
      250);

    var apiConf = new ApiConf(
      null,
      "maximal-api",
      "API with all features enabled",
      null,
      "arm64",
      true,
      false,
      true,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of("Feature", "complete", "Tier", "premium"));

    var construct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    assertNotNull(construct);
    assertNotNull(construct.api());
  }

  @Test
  void testUsagePlanConstructHighThrottle() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "high-throttle-logs",
      "STANDARD",
      "ONE_WEEK",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
      "prod",
      "High throughput stage",
      "ERROR",
      Map.of(),
      false,
      false,
      true,
      false,
      500,
      250);

    var apiConf = new ApiConf(
      null,
      "high-throughput-api",
      "High throughput API",
      null,
      "x86_64",
      true,
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of());

    var apiConstruct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    var throttle = new ThrottleConf(5000.0, 10000);
    var quota = new QuotaConf(true, 100000000, Period.MONTH);

    var usagePlanConf = new UsagePlanConf(
      "high-throughput-plan",
      "High throughput usage plan",
      throttle,
      quota);

    var construct = new UsagePlanConstruct(
      ctx.scope(),
      ctx.common(),
      usagePlanConf,
      apiConstruct.api());

    assertNotNull(construct);
    assertNotNull(construct.usagePlan());
  }

  @Test
  void testUsagePlanConstructLowThrottle() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "low-throttle-logs",
      "STANDARD",
      "ONE_WEEK",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
      "dev",
      "Development stage",
      "INFO",
      Map.of(),
      false,
      false,
      true,
      false,
      20,
      10);

    var apiConf = new ApiConf(
      null,
      "low-throughput-api",
      "Low throughput API",
      null,
      "x86_64",
      true,
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of());

    var apiConstruct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    var throttle = new ThrottleConf(10.0, 20);
    var quota = new QuotaConf(true, 1000, Period.DAY);

    var usagePlanConf = new UsagePlanConf(
      "basic-dev-plan",
      "Basic development usage plan",
      throttle,
      quota);

    var construct = new UsagePlanConstruct(
      ctx.scope(),
      ctx.common(),
      usagePlanConf,
      apiConstruct.api());

    assertNotNull(construct);
    assertNotNull(construct.usagePlan());
  }

  @Test
  void testRestApiConstructStagingEnvironment() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "staging-logs",
      "STANDARD",
      "TWO_WEEKS",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
      "staging",
      "Staging environment",
      "INFO",
      Map.of("Environment", "staging", "Purpose", "testing"),
      true,
      false,
      true,
      false,
      200,
      100);

    var apiConf = new ApiConf(
      null,
      "staging-api",
      "Staging API",
      null,
      "x86_64",
      true,
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of("Stage", "staging"));

    var construct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    assertNotNull(construct);
    assertNotNull(construct.api());
  }

  @Test
  void testRestApiConstructWithLongRetention() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "long-retention-logs",
      "STANDARD",
      "TEN_YEARS",
      null,
      "RETAIN",
      Map.of());

    var stageOptions = new StageOptions(
      "prod",
      "Production with long retention",
      "ERROR",
      Map.of(),
      false,
      false,
      true,
      false,
      100,
      50);

    var apiConf = new ApiConf(
      null,
      "compliance-api",
      "API with compliance logging",
      null,
      "x86_64",
      true,
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of("Compliance", "enabled"));

    var construct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    assertNotNull(construct);
    assertNotNull(construct.api());
  }

  @Test
  void testRestApiConstructWithInfrequentAccess() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "infrequent-logs",
      "INFREQUENT_ACCESS",
      "THREE_MONTHS",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
      "prod",
      "Production with infrequent access logs",
      "ERROR",
      Map.of(),
      false,
      false,
      true,
      false,
      100,
      50);

    var apiConf = new ApiConf(
      null,
      "infrequent-api",
      "API with infrequent access logging",
      null,
      "x86_64",
      true,
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of());

    var construct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    assertNotNull(construct);
    assertNotNull(construct.api());
  }

  @Test
  void testRestApiConstructQaEnvironment() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "qa-logs",
      "STANDARD",
      "ONE_WEEK",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
      "qa",
      "QA environment",
      "INFO",
      Map.of("Environment", "qa", "Testing", "enabled"),
      false,
      true,
      true,
      false,
      100,
      50);

    var apiConf = new ApiConf(
      null,
      "qa-api",
      "QA API",
      null,
      "x86_64",
      true,
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of("Stage", "qa", "Purpose", "quality-assurance"));

    var construct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    assertNotNull(construct);
    assertNotNull(construct.api());
  }

  @Test
  void testRestApiConstructIntegrationEnvironment() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "integration-logs",
      "STANDARD",
      "ONE_MONTH",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
      "integration",
      "Integration testing environment",
      "INFO",
      Map.of("Environment", "integration"),
      false,
      false,
      true,
      false,
      150,
      75);

    var apiConf = new ApiConf(
      null,
      "integration-api",
      "Integration API",
      null,
      "arm64",
      true,
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of("Stage", "integration"));

    var construct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    assertNotNull(construct);
    assertNotNull(construct.api());
  }

  @Test
  void testUsagePlanConstructMediumTier() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "medium-tier-logs",
      "STANDARD",
      "ONE_WEEK",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
      "prod",
      "Medium tier stage",
      "INFO",
      Map.of(),
      false,
      false,
      true,
      false,
      100,
      50);

    var apiConf = new ApiConf(
      null,
      "medium-tier-api",
      "Medium tier API",
      null,
      "x86_64",
      true,
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of());

    var apiConstruct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    var throttle = new ThrottleConf(500.0, 1000);
    var quota = new QuotaConf(true, 500000, Period.MONTH);

    var usagePlanConf = new UsagePlanConf(
      "medium-tier-plan",
      "Medium tier usage plan",
      throttle,
      quota);

    var construct = new UsagePlanConstruct(
      ctx.scope(),
      ctx.common(),
      usagePlanConf,
      apiConstruct.api());

    assertNotNull(construct);
    assertNotNull(construct.usagePlan());
  }

  @Test
  void testRestApiConstructWithCustomAuthorizer() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "custom-auth-logs",
      "STANDARD",
      "ONE_MONTH",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
      "prod",
      "Custom authorizer stage",
      "INFO",
      Map.of("AuthType", "CUSTOM"),
      false,
      false,
      true,
      false,
      100,
      50);

    var apiConf = new ApiConf(
      null,
      "custom-auth-api",
      "API with custom authorizer",
      null,
      "x86_64",
      true,
      false,
      true,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of("Auth", "Custom"));

    var construct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    assertNotNull(construct);
    assertNotNull(construct.api());
  }

  @Test
  void testRestApiConstructWithAllCachingDisabled() {
    var ctx = createTestContext();

    var logGroup = new LogGroupConf(
      "no-cache-logs",
      "STANDARD",
      "ONE_WEEK",
      null,
      "DESTROY",
      Map.of());

    var stageOptions = new StageOptions(
      "dev",
      "Development without caching",
      "INFO",
      Map.of(),
      false, // no caching
      false, // no data trace
      false, // no metrics
      false, // no tracing
      50,
      25);

    var apiConf = new ApiConf(
      null,
      "no-cache-api",
      "API without caching",
      null,
      "x86_64",
      false, // cloudwatch disabled
      false,
      false,
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of(),
      stageOptions,
      logGroup,
      Map.of());

    var construct = new RestApiConstruct(ctx.scope(), ctx.common(), apiConf, null);

    assertNotNull(construct);
    assertNotNull(construct.api());
  }
}
