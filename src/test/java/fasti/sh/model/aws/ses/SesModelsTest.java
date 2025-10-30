package fasti.sh.model.aws.ses;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for SES model records.
 */
class SesModelsTest {

  @Test
  void testSenderBasic() {
    var sender = new Sender(
      "us-east-1",
      "example.com",
      "Support Team",
      "support@example.com",
      "noreply@example.com",
      "default-config");

    assertEquals("us-east-1", sender.sesRegion());
    assertEquals("example.com", sender.sesVerifiedDomain());
    assertEquals("Support Team", sender.fromName());
    assertEquals("support@example.com", sender.fromEmail());
    assertEquals("noreply@example.com", sender.replyTo());
    assertEquals("default-config", sender.configurationSetName());
    assertRecordToString(sender);
  }

  @Test
  void testSenderDifferentRegion() {
    var sender = new Sender(
      "eu-west-1",
      "myapp.io",
      "Marketing",
      "marketing@myapp.io",
      "support@myapp.io",
      "marketing-config");

    assertEquals("eu-west-1", sender.sesRegion());
    assertEquals("myapp.io", sender.sesVerifiedDomain());
  }

  @Test
  void testBounceEnabled() {
    var bounce = new Bounce(true, "bounce-topic", "bounce-config");

    assertTrue(bounce.enabled());
    assertEquals("bounce-topic", bounce.topic());
    assertEquals("bounce-config", bounce.configurationSet());
    assertRecordToString(bounce);
  }

  @Test
  void testBounceDisabled() {
    var bounce = new Bounce(false, null, null);

    assertFalse(bounce.enabled());
    assertNull(bounce.topic());
  }

  @Test
  void testRejectEnabled() {
    var reject = new Reject(true, "reject-topic", "reject-config");

    assertTrue(reject.enabled());
    assertEquals("reject-topic", reject.topic());
    assertRecordToString(reject);
  }

  @Test
  void testRejectDisabled() {
    var reject = new Reject(false, null, null);

    assertFalse(reject.enabled());
  }

  @Test
  void testComplaintEnabled() {
    var complaint = new Complaint(true, "complaint-topic", "complaint-config");

    assertTrue(complaint.enabled());
    assertEquals("complaint-topic", complaint.topic());
    assertRecordToString(complaint);
  }

  @Test
  void testComplaintDisabled() {
    var complaint = new Complaint(false, null, null);

    assertFalse(complaint.enabled());
  }

  @Test
  void testDestination() {
    var bounce = new Bounce(true, "bounce-topic", "bounce-config");
    var reject = new Reject(true, "reject-topic", "reject-config");
    var complaint = new Complaint(true, "complaint-topic", "complaint-config");

    var destination = new Destination(bounce, reject, complaint);

    assertNotNull(destination.bounce());
    assertNotNull(destination.reject());
    assertNotNull(destination.complaint());
    assertTrue(destination.bounce().enabled());
    assertTrue(destination.reject().enabled());
    assertTrue(destination.complaint().enabled());
    assertRecordToString(destination);
  }

  @Test
  void testDestinationDisabled() {
    var bounce = new Bounce(false, null, null);
    var reject = new Reject(false, null, null);
    var complaint = new Complaint(false, null, null);

    var destination = new Destination(bounce, reject, complaint);

    assertFalse(destination.bounce().enabled());
    assertFalse(destination.reject().enabled());
    assertFalse(destination.complaint().enabled());
  }

  @Test
  void testDedicatedIpPoolEnabled() {
    var pool = new DedicatedIpPool(true, "my-pool", "STANDARD");

    assertTrue(pool.enabled());
    assertEquals("my-pool", pool.name());
    assertEquals("STANDARD", pool.scalingMode());
    assertRecordToString(pool);
  }

  @Test
  void testDedicatedIpPoolDisabled() {
    var pool = new DedicatedIpPool(false, null, null);

    assertFalse(pool.enabled());
    assertNull(pool.name());
  }

  @Test
  void testDedicatedIpPoolManaged() {
    var pool = new DedicatedIpPool(true, "managed-pool", "MANAGED");

    assertEquals("MANAGED", pool.scalingMode());
  }

  @Test
  void testConfigurationSetBasic() {
    var pool = new DedicatedIpPool(false, null, null);
    var config = new ConfigurationSetConf(
      "my-config-set",
      null,
      true,
      true,
      "REQUIRE",
      "BOUNCE",
      pool,
      Map.of());

    assertEquals("my-config-set", config.name());
    assertTrue(config.reputationMetrics());
    assertTrue(config.sendingEnabled());
    assertEquals("REQUIRE", config.tlsPolicyConfiguration());
    assertEquals("BOUNCE", config.suppressionReasons());
    assertRecordToString(config);
  }

  @Test
  void testConfigurationSetWithPool() {
    var pool = new DedicatedIpPool(true, "prod-pool", "STANDARD");
    var config = new ConfigurationSetConf(
      "prod-config",
      "track.example.com",
      true,
      true,
      "OPTIONAL",
      "COMPLAINT",
      pool,
      Map.of("Environment", "production"));

    assertTrue(config.dedicatedIpPool().enabled());
    assertEquals("prod-pool", config.dedicatedIpPool().name());
    assertEquals("track.example.com", config.customTrackingRedirectDomain());
    assertEquals(1, config.tags().size());
  }

  @Test
  void testConfigurationSetDisabled() {
    var pool = new DedicatedIpPool(false, null, null);
    var config = new ConfigurationSetConf(
      "disabled-config",
      null,
      false,
      false,
      null,
      null,
      pool,
      Map.of());

    assertFalse(config.reputationMetrics());
    assertFalse(config.sendingEnabled());
  }
}
