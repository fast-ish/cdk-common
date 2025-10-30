package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.ses.IdentityConstruct;
import fasti.sh.model.aws.ses.ConfigurationSetConf;
import fasti.sh.model.aws.ses.DedicatedIpPool;
import fasti.sh.model.aws.ses.IdentityConf;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for SES constructs.
 */
class SesConstructsTest {

  @Test
  void testIdentityConstructBasic() {
    var ctx = createTestContext();

    var dedicatedIpPool = new DedicatedIpPool(false, "", "");
    var configSet = new ConfigurationSetConf(
      "test-config-set",
      null,
      true,
      true,
      "REQUIRE",
      "BOUNCES_AND_COMPLAINTS",
      dedicatedIpPool,
      Map.of());

    var identityConf = new IdentityConf(
      "Z1234567890ABC",
      "test@example.com",
      "example.com",
      "USE_DEFAULT_VALUE",
      "mail.example.com",
      true,
      configSet);

    var construct = new IdentityConstruct(ctx.scope(), ctx.common(), identityConf);

    assertNotNull(construct);
    assertNotNull(construct.hostedZoneIdentity());
    assertNotNull(construct.configurationSet());
    assertNotNull(construct.emailIdentity());
    assertNotNull(construct.mxInboundRecord());
  }

  @Test
  void testIdentityConstructWithDedicatedIpPool() {
    var ctx = createTestContext();

    var dedicatedIpPool = new DedicatedIpPool(true, "my-dedicated-pool", "STANDARD");
    var configSet = new ConfigurationSetConf(
      "test-config-dedicated",
      "tracking.example.com",
      true,
      true,
      "REQUIRE",
      "BOUNCES_AND_COMPLAINTS",
      dedicatedIpPool,
      Map.of("Type", "production"));

    var identityConf = new IdentityConf(
      "Z9876543210XYZ",
      "noreply@example.com",
      "example.com",
      "REJECT_MESSAGE",
      "bounce.example.com",
      false,
      configSet);

    var construct = new IdentityConstruct(ctx.scope(), ctx.common(), identityConf);

    assertNotNull(construct);
    assertNotNull(construct.hostedZoneIdentity());
    assertNotNull(construct.configurationSet());
  }

  @Test
  void testIdentityConstructOptionalTls() {
    var ctx = createTestContext();

    var dedicatedIpPool = new DedicatedIpPool(false, "", "");
    var configSet = new ConfigurationSetConf(
      "optional-tls-config",
      null,
      false,
      true,
      "OPTIONAL",
      "BOUNCES_AND_COMPLAINTS",
      dedicatedIpPool,
      Map.of());

    var identityConf = new IdentityConf(
      "Z1111111111111",
      "support@example.org",
      "example.org",
      "USE_DEFAULT_VALUE",
      "mail.example.org",
      true,
      configSet);

    var construct = new IdentityConstruct(ctx.scope(), ctx.common(), identityConf);

    assertNotNull(construct);
    assertNotNull(construct.hostedZoneIdentity());
  }

  @Test
  void testIdentityConstructSendingDisabled() {
    var ctx = createTestContext();

    var dedicatedIpPool = new DedicatedIpPool(false, "", "");
    var configSet = new ConfigurationSetConf(
      "disabled-sending-config",
      null,
      true,
      false, // sending disabled
      "REQUIRE",
      "BOUNCES_AND_COMPLAINTS",
      dedicatedIpPool,
      Map.of("Monitoring", "enabled"));

    var identityConf = new IdentityConf(
      "Z2222222222222",
      "admin@test.com",
      "test.com",
      "USE_DEFAULT_VALUE",
      "mail.test.com",
      false,
      configSet);

    var construct = new IdentityConstruct(ctx.scope(), ctx.common(), identityConf);

    assertNotNull(construct);
    assertNotNull(construct.emailIdentity());
    assertNotNull(construct.mxInboundRecord());
  }

  @Test
  void testIdentityConstructBouncesOnly() {
    var ctx = createTestContext();

    var dedicatedIpPool = new DedicatedIpPool(false, "", "");
    var configSet = new ConfigurationSetConf(
      "bounces-only-config",
      "track.example.net",
      true,
      true,
      "REQUIRE",
      "BOUNCES_ONLY",
      dedicatedIpPool,
      Map.of("Suppression", "bounces"));

    var identityConf = new IdentityConf(
      "Z3333333333333",
      "contact@example.net",
      "example.net",
      "USE_DEFAULT_VALUE",
      "mail.example.net",
      true,
      configSet);

    var construct = new IdentityConstruct(ctx.scope(), ctx.common(), identityConf);

    assertNotNull(construct);
    assertNotNull(construct.configurationSet());
  }

  @Test
  void testIdentityConstructComplaintsOnly() {
    var ctx = createTestContext();

    var dedicatedIpPool = new DedicatedIpPool(false, "", "");
    var configSet = new ConfigurationSetConf(
      "complaints-only-config",
      null,
      true,
      true,
      "OPTIONAL",
      "COMPLAINTS_ONLY",
      dedicatedIpPool,
      Map.of("Suppression", "complaints"));

    var identityConf = new IdentityConf(
      "Z4444444444444",
      "info@demo.com",
      "demo.com",
      "REJECT_MESSAGE",
      "bounce.demo.com",
      false,
      configSet);

    var construct = new IdentityConstruct(ctx.scope(), ctx.common(), identityConf);

    assertNotNull(construct);
    assertNotNull(construct.hostedZoneIdentity());
    assertNotNull(construct.emailIdentity());
  }

  @Test
  void testIdentityConstructWithCustomTracking() {
    var ctx = createTestContext();

    var dedicatedIpPool = new DedicatedIpPool(true, "premium-pool", "STANDARD");
    var configSet = new ConfigurationSetConf(
      "custom-tracking-config",
      "clicks.example.io",
      false,
      true,
      "REQUIRE",
      "BOUNCES_AND_COMPLAINTS",
      dedicatedIpPool,
      Map.of("Tracking", "custom", "Pool", "premium"));

    var identityConf = new IdentityConf(
      "Z5555555555555",
      "notifications@example.io",
      "example.io",
      "USE_DEFAULT_VALUE",
      "smtp.example.io",
      true,
      configSet);

    var construct = new IdentityConstruct(ctx.scope(), ctx.common(), identityConf);

    assertNotNull(construct);
    assertNotNull(construct.configurationSet());
    assertNotNull(construct.hostedZoneIdentity());
  }
}
