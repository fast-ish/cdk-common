package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.cloudwatch.LogGroupConstruct;
import fasti.sh.model.aws.cloudwatch.LogGroupConf;
import fasti.sh.model.aws.kms.Kms;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for CloudWatch constructs.
 */
class CloudWatchConstructsTest {

  @Test
  void testLogGroupBasic() {
    var ctx = createTestContext();

    var logGroupConf = new LogGroupConf(
      "test-log-group",
      "STANDARD",
      "ONE_WEEK",
      null,
      "DESTROY",
      Map.of());

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), logGroupConf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupWithKms() {
    var ctx = createTestContext();

    var kms = new Kms(
      "logs-key",
      "Log encryption key",
      true,
      true,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "RETAIN");

    var logGroupConf = new LogGroupConf(
      "encrypted-logs",
      "STANDARD",
      "ONE_MONTH",
      kms,
      "RETAIN",
      Map.of("Encrypted", "true"));

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), logGroupConf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupInfrequentAccess() {
    var ctx = createTestContext();

    var logGroupConf = new LogGroupConf(
      "infrequent-logs",
      "INFREQUENT_ACCESS",
      "SIX_MONTHS",
      null,
      "DESTROY",
      Map.of());

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), logGroupConf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupLongRetention() {
    var ctx = createTestContext();

    var logGroupConf = new LogGroupConf(
      "archive-logs",
      "STANDARD",
      "TEN_YEARS",
      null,
      "RETAIN",
      Map.of("Archive", "true"));

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), logGroupConf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupOneDay() {
    var ctx = createTestContext();

    var logGroupConf = new LogGroupConf(
      "daily-logs",
      "STANDARD",
      "ONE_DAY",
      null,
      "DESTROY",
      Map.of("Retention", "daily"));

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), logGroupConf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupThreeDays() {
    var ctx = createTestContext();

    var logGroupConf = new LogGroupConf(
      "short-term-logs",
      "STANDARD",
      "THREE_DAYS",
      null,
      "DESTROY",
      Map.of());

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), logGroupConf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupFiveYears() {
    var ctx = createTestContext();

    var logGroupConf = new LogGroupConf(
      "long-archive-logs",
      "INFREQUENT_ACCESS",
      "FIVE_YEARS",
      null,
      "RETAIN",
      Map.of("Archive", "long-term"));

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), logGroupConf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupTwoWeeks() {
    var ctx = createTestContext();

    var logGroupConf = new LogGroupConf(
      "temp-logs",
      "STANDARD",
      "TWO_WEEKS",
      null,
      "DESTROY",
      Map.of());

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), logGroupConf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupTenYears() {
    var ctx = createTestContext();

    var logGroupConf = new LogGroupConf(
      "decade-logs",
      "INFREQUENT_ACCESS",
      "TEN_YEARS",
      null,
      "RETAIN",
      Map.of("Retention", "decade"));

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), logGroupConf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupOneMonth() {
    var ctx = createTestContext();

    var logGroupConf = new LogGroupConf(
      "monthly-logs",
      "STANDARD",
      "ONE_MONTH",
      null,
      "DESTROY",
      Map.of("Period", "monthly"));

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), logGroupConf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupSixMonths() {
    var ctx = createTestContext();

    var logGroupConf = new LogGroupConf(
      "semi-annual-logs",
      "STANDARD",
      "SIX_MONTHS",
      null,
      "DESTROY",
      Map.of("Period", "6months"));

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), logGroupConf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupWithKmsEncryption() {
    var ctx = createTestContext();

    var kmsConf = new Kms(
      "cloudwatch-key",
      "CloudWatch encryption key",
      true,
      false,
      "ENCRYPT_DECRYPT",
      "SYMMETRIC_DEFAULT",
      "DESTROY");

    var logGroupConf = new LogGroupConf(
      "encrypted-logs",
      "STANDARD",
      "ONE_YEAR",
      kmsConf,
      "DESTROY",
      Map.of("Encrypted", "true"));

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), logGroupConf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupInfrequentWithRetain() {
    var ctx = createTestContext();

    var logGroupConf = new LogGroupConf(
      "archive-logs",
      "INFREQUENT_ACCESS",
      "TWO_YEARS",
      null,
      "RETAIN",
      Map.of("Archive", "true", "Class", "infrequent"));

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), logGroupConf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }
}
