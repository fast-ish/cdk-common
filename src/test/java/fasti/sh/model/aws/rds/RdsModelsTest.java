package fasti.sh.model.aws.rds;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.secretsmanager.SecretCredentials;
import fasti.sh.model.aws.secretsmanager.SecretFormat;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for RDS model records.
 */
class RdsModelsTest {

  @Test
  void testRdsPerformanceInsightsEnabled() {
    var pi = new RdsPerformanceInsights(true, "7");

    assertTrue(pi.enabled());
    assertEquals("7", pi.retention());
    assertRecordToString(pi);
  }

  @Test
  void testRdsPerformanceInsightsDisabled() {
    var pi = new RdsPerformanceInsights(false, null);

    assertFalse(pi.enabled());
    assertNull(pi.retention());
  }

  @Test
  void testRdsPerformanceInsightsLongRetention() {
    var pi = new RdsPerformanceInsights(true, "731");

    assertTrue(pi.enabled());
    assertEquals("731", pi.retention());
  }

  @Test
  void testRdsWriterBasic() {
    var pi = new RdsPerformanceInsights(false, null);
    var writer = new RdsWriter(
      false,
      true,
      "primary-db",
      false,
      pi);

    assertEquals("primary-db", writer.name());
    assertFalse(writer.allowMajorVersionUpgrade());
    assertTrue(writer.autoMinorVersionUpgrade());
    assertFalse(writer.publiclyAccessible());
    assertNotNull(writer.performanceInsights());
    assertRecordToString(writer);
  }

  @Test
  void testRdsWriterPublic() {
    var pi = new RdsPerformanceInsights(true, "7");
    var writer = new RdsWriter(
      true,
      true,
      "public-db",
      true,
      pi);

    assertTrue(writer.publiclyAccessible());
    assertTrue(writer.allowMajorVersionUpgrade());
  }

  @Test
  void testRdsWriterWithPerformanceInsights() {
    var pi = new RdsPerformanceInsights(true, "14");
    var writer = new RdsWriter(
      false,
      false,
      "monitored-db",
      false,
      pi);

    assertTrue(writer.performanceInsights().enabled());
    assertEquals("14", writer.performanceInsights().retention());
  }

  @Test
  void testRdsReaderBasic() {
    var pi = new RdsPerformanceInsights(false, null);
    var reader = new RdsReader(
      false,
      true,
      "read-replica",
      false,
      false,
      pi);

    assertEquals("read-replica", reader.name());
    assertFalse(reader.allowMajorVersionUpgrade());
    assertTrue(reader.autoMinorVersionUpgrade());
    assertFalse(reader.publiclyAccessible());
    assertFalse(reader.scaleWithWriter());
    assertRecordToString(reader);
  }

  @Test
  void testRdsReaderScaling() {
    var pi = new RdsPerformanceInsights(true, "7");
    var reader = new RdsReader(
      false,
      true,
      "scaling-replica",
      false,
      true,
      pi);

    assertTrue(reader.scaleWithWriter());
    assertEquals("scaling-replica", reader.name());
  }

  @Test
  void testRdsReaderPublic() {
    var pi = new RdsPerformanceInsights(false, null);
    var reader = new RdsReader(
      true,
      false,
      "public-replica",
      true,
      false,
      pi);

    assertTrue(reader.publiclyAccessible());
    assertTrue(reader.allowMajorVersionUpgrade());
    assertFalse(reader.autoMinorVersionUpgrade());
  }

  @Test
  void testRdsPerformanceInsightsEquality() {
    var pi1 = new RdsPerformanceInsights(true, "7");
    var pi2 = new RdsPerformanceInsights(true, "7");

    assertRecordEquality(pi1, pi2);
  }

  @Test
  void testRdsBasic() {
    var passwordFormat = new SecretFormat(false, false, false, false, 32, true);
    var credentials = new SecretCredentials(
      "rds-credentials",
      "RDS database credentials",
      "admin",
      passwordFormat,
      "DESTROY",
      Map.of());
    var pi = new RdsPerformanceInsights(false, null);
    var writer = new RdsWriter(false, true, "primary", false, pi);

    var rds = new Rds(
      "15.4",
      "my-database",
      "appdb",
      credentials,
      "gp3",
      false,
      writer,
      List.of(),
      false,
      "DESTROY",
      Map.of());

    assertEquals("15.4", rds.version());
    assertEquals("my-database", rds.name());
    assertEquals("appdb", rds.databaseName());
    assertEquals("gp3", rds.storageType());
    assertFalse(rds.enableDataApi());
    assertFalse(rds.deletionProtection());
    assertTrue(rds.readers().isEmpty());
    assertRecordToString(rds);
  }

  @Test
  void testRdsWithReaders() {
    var passwordFormat = new SecretFormat(false, false, false, false, 32, true);
    var credentials = new SecretCredentials(
      "prod-credentials",
      "Production credentials",
      "admin",
      passwordFormat,
      "RETAIN",
      Map.of());
    var pi = new RdsPerformanceInsights(true, "7");
    var writer = new RdsWriter(false, true, "primary", false, pi);
    var reader1 = new RdsReader(false, true, "replica-1", false, true, pi);
    var reader2 = new RdsReader(false, true, "replica-2", false, true, pi);

    var rds = new Rds(
      "15.4",
      "prod-database",
      "proddb",
      credentials,
      "io1",
      true,
      writer,
      List.of(reader1, reader2),
      true,
      "RETAIN",
      Map.of("Environment", "production"));

    assertEquals(2, rds.readers().size());
    assertTrue(rds.enableDataApi());
    assertTrue(rds.deletionProtection());
    assertEquals("RETAIN", rds.removalPolicy());
    assertEquals("replica-1", rds.readers().get(0).name());
  }

  @Test
  void testRdsWithDataApi() {
    var passwordFormat = new SecretFormat(false, false, false, false, 32, true);
    var credentials = new SecretCredentials(
      "serverless-credentials",
      "Serverless DB credentials",
      "admin",
      passwordFormat,
      "DESTROY",
      Map.of());
    var pi = new RdsPerformanceInsights(false, null);
    var writer = new RdsWriter(false, true, "primary", false, pi);

    var rds = new Rds(
      "13.9",
      "serverless-db",
      "serverless",
      credentials,
      "aurora",
      true,
      writer,
      List.of(),
      false,
      "DESTROY",
      Map.of("Type", "aurora-serverless"));

    assertTrue(rds.enableDataApi());
    assertEquals("aurora", rds.storageType());
  }
}
