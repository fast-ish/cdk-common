package fasti.sh.execute.aws.codebuild;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.codebuild.Certificate;
import fasti.sh.model.aws.codebuild.Environment;
import fasti.sh.model.aws.s3.S3Bucket;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.codebuild.ComputeType;
import software.amazon.awscdk.services.codebuild.LinuxBuildImage;
import software.amazon.awscdk.services.s3.BucketAccessControl;
import software.amazon.awscdk.services.s3.ObjectOwnership;

/**
 * Tests for CodeBuild constructs and utilities.
 */
class CodeBuildConstructsTest {

  @Test
  void testDecideBuildEnvironmentWithoutCertificate() {
    var ctx = createTestContext();

    var environment = new Environment(
      ComputeType.SMALL,
      Map.of("ENV_VAR", "value"),
      false,
      new Certificate(null, null));

    var buildEnv = DecideBuildEnvironment
      .from(
        ctx.scope(),
        ctx.common(),
        environment,
        LinuxBuildImage.STANDARD_7_0);

    assertNotNull(buildEnv);
    assertNotNull(buildEnv.getBuildImage());
    assertEquals(ComputeType.SMALL, buildEnv.getComputeType());
    assertFalse(buildEnv.getPrivileged());
  }

  @Test
  void testDecideBuildEnvironmentWithCertificate() {
    var ctx = createTestContext();

    var s3Bucket = new S3Bucket(
      "cert-bucket",
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      true,
      false,
      false,
      software.amazon.awscdk.RemovalPolicy.DESTROY,
      null,
      Map.of());

    var environment = new Environment(
      ComputeType.MEDIUM,
      Map.of("BUILD_ENV", "production"),
      true,
      new Certificate(s3Bucket, "certificates/build-cert.pem"));

    var buildEnv = DecideBuildEnvironment
      .from(
        ctx.scope(),
        ctx.common(),
        environment,
        LinuxBuildImage.STANDARD_7_0);

    assertNotNull(buildEnv);
    assertEquals(ComputeType.MEDIUM, buildEnv.getComputeType());
    assertTrue(buildEnv.getPrivileged());
  }

  @Test
  void testDecideBuildEnvironmentSmallCompute() {
    var ctx = createTestContext();

    var environment = new Environment(
      ComputeType.SMALL,
      Map.of(),
      false,
      new Certificate(null, null));

    var buildEnv = DecideBuildEnvironment
      .from(
        ctx.scope(),
        ctx.common(),
        environment,
        LinuxBuildImage.AMAZON_LINUX_2_5);

    assertNotNull(buildEnv);
    assertEquals(ComputeType.SMALL, buildEnv.getComputeType());
  }

  @Test
  void testDecideBuildEnvironmentLargeCompute() {
    var ctx = createTestContext();

    var environment = new Environment(
      ComputeType.LARGE,
      Map.of("KEY1", "val1", "KEY2", "val2"),
      true,
      new Certificate(null, null));

    var buildEnv = DecideBuildEnvironment
      .from(
        ctx.scope(),
        ctx.common(),
        environment,
        LinuxBuildImage.STANDARD_7_0);

    assertNotNull(buildEnv);
    assertEquals(ComputeType.LARGE, buildEnv.getComputeType());
    assertTrue(buildEnv.getPrivileged());
  }

  @Test
  void testDecideBuildEnvironmentEmptyVariables() {
    var ctx = createTestContext();

    var environment = new Environment(
      ComputeType.MEDIUM,
      Map.of(),
      false,
      new Certificate(null, null));

    var buildEnv = DecideBuildEnvironment
      .from(
        ctx.scope(),
        ctx.common(),
        environment,
        LinuxBuildImage.STANDARD_7_0);

    assertNotNull(buildEnv);
    assertEquals(ComputeType.MEDIUM, buildEnv.getComputeType());
  }

  @Test
  void testEnvironmentVariablesMapping() {
    var environment = new Environment(
      ComputeType.SMALL,
      Map.of("KEY1", "value1", "KEY2", "value2", "KEY3", "value3"),
      false,
      new Certificate(null, null));

    var envVars = environment.environmentVariables();

    assertNotNull(envVars);
    assertEquals(3, envVars.size());
    assertTrue(envVars.containsKey("KEY1"));
    assertTrue(envVars.containsKey("KEY2"));
    assertTrue(envVars.containsKey("KEY3"));
  }

  @Test
  void testEnvironmentVariablesEmpty() {
    var environment = new Environment(
      ComputeType.SMALL,
      Map.of(),
      false,
      new Certificate(null, null));

    var envVars = environment.environmentVariables();

    assertNotNull(envVars);
    assertTrue(envVars.isEmpty());
  }
}
