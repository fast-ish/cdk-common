package fasti.sh.execute.aws.cloudwatch;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.cloudwatch.LogGroupConf;
import fasti.sh.model.aws.kms.Kms;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for LogGroupConstruct.
 */
class LogGroupConstructTest {

  @Test
  void testLogGroupConstructMinimal() {
    var ctx = createTestContext();

    var conf = new LogGroupConf(
      "/aws/test/minimal",
      "standard",
      "one_week",
      null,
      "destroy",
      Map.of());

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), conf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupConstructInfrequentAccess() {
    var ctx = createTestContext();

    var conf = new LogGroupConf(
      "/aws/test/infrequent",
      "infrequent_access",
      "one_month",
      null,
      "destroy",
      Map.of());

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), conf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupConstructWithRetention() {
    var ctx = createTestContext();

    var conf = new LogGroupConf(
      "/aws/test/retention",
      "standard",
      "six_months",
      null,
      "retain",
      Map.of());

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), conf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupConstructWithKms() {
    var ctx = createTestContext();

    var kms = new Kms(
      "alias/test-log-encryption",
      "KMS key for log encryption",
      true,
      false,
      "encrypt_decrypt",
      "symmetric_default",
      "destroy");

    var conf = new LogGroupConf(
      "/aws/test/encrypted",
      "standard",
      "one_week",
      kms,
      "destroy",
      Map.of());

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), conf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }

  @Test
  void testLogGroupConstructWithTags() {
    var ctx = createTestContext();

    var conf = new LogGroupConf(
      "/aws/test/tagged",
      "standard",
      "one_week",
      null,
      "destroy",
      Map.of("Application", "TestApp", "Component", "Logging"));

    var construct = new LogGroupConstruct(ctx.scope(), ctx.common(), conf);

    assertNotNull(construct);
    assertNotNull(construct.logGroup());
  }
}
