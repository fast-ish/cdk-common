package fasti.sh.model.aws.loadbalancer;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests for Load Balancer model records.
 */
class LoadBalancerModelsTest {

  @Test
  void testHealthCheckBasic() {
    var healthCheck = new HealthCheck(
      true,
      "200",
      "/health",
      "80",
      "HTTP");

    assertTrue(healthCheck.enabled());
    assertEquals("200", healthCheck.healthyHttpCodes());
    assertEquals("/health", healthCheck.path());
    assertEquals("80", healthCheck.port());
    assertEquals("HTTP", healthCheck.protocol());
    assertRecordToString(healthCheck);
  }

  @Test
  void testHealthCheckHttps() {
    var healthCheck = new HealthCheck(
      true,
      "200,201",
      "/api/health",
      "443",
      "HTTPS");

    assertEquals("HTTPS", healthCheck.protocol());
    assertEquals("443", healthCheck.port());
    assertEquals("200,201", healthCheck.healthyHttpCodes());
  }

  @Test
  void testHealthCheckTcp() {
    var healthCheck = new HealthCheck(
      true,
      null,
      null,
      "8080",
      "TCP");

    assertEquals("TCP", healthCheck.protocol());
    assertNull(healthCheck.path());
    assertNull(healthCheck.healthyHttpCodes());
  }

  @Test
  void testHealthCheckDisabled() {
    var healthCheck = new HealthCheck(
      false,
      null,
      null,
      null,
      null);

    assertFalse(healthCheck.enabled());
  }

  @Test
  void testHealthCheckMultipleCodes() {
    var healthCheck = new HealthCheck(
      true,
      "200,201,202,204",
      "/status",
      "8080",
      "HTTP");

    assertEquals("200,201,202,204", healthCheck.healthyHttpCodes());
  }

  @Test
  void testHealthCheckEquality() {
    var hc1 = new HealthCheck(true, "200", "/health", "80", "HTTP");
    var hc2 = new HealthCheck(true, "200", "/health", "80", "HTTP");

    assertRecordEquality(hc1, hc2);
  }
}
