package fasti.sh.model.aws.cdk;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests for CDK model records.
 */
class CdkModelsTest {

  @Test
  void testSynthesizerBasic() {
    var synthesizer = new Synthesizer(
      "arn:aws:iam::123456789012:role/cdk-exec",
      "arn:aws:iam::123456789012:role/cdk-deploy",
      "arn:aws:iam::123456789012:role/cdk-file-publish",
      "arn:aws:iam::123456789012:role/cdk-image-publish",
      "arn:aws:iam::123456789012:role/cdk-lookup",
      "hnb659fds",
      "cdk-",
      null,
      "cdk",
      null,
      "cdk-assets-bucket",
      null,
      "cdk-assets-repo",
      null,
      "/cdk-bootstrap/hnb659fds/version",
      true,
      false);

    assertEquals("arn:aws:iam::123456789012:role/cdk-exec", synthesizer.cloudFormationExecutionRole());
    assertEquals("arn:aws:iam::123456789012:role/cdk-deploy", synthesizer.deployRoleArn());
    assertEquals("hnb659fds", synthesizer.qualifier());
    assertEquals("cdk-assets-bucket", synthesizer.fileAssetsBucketName());
    assertEquals("cdk-assets-repo", synthesizer.imageAssetsRepositoryName());
    assertTrue(synthesizer.generateBootstrapVersionRule());
    assertFalse(synthesizer.useLookupRoleForStackOperations());
    assertRecordToString(synthesizer);
  }

  @Test
  void testSynthesizerWithExternalIds() {
    var synthesizer = new Synthesizer(
      "arn:aws:iam::123456789012:role/cdk-exec",
      "arn:aws:iam::123456789012:role/cdk-deploy",
      "arn:aws:iam::123456789012:role/cdk-file-publish",
      "arn:aws:iam::123456789012:role/cdk-image-publish",
      "arn:aws:iam::123456789012:role/cdk-lookup",
      "custom123",
      "custom-cdk-",
      "deploy-external-id",
      "custom",
      "file-external-id",
      "custom-assets-bucket",
      "image-external-id",
      "custom-assets-repo",
      "lookup-external-id",
      "/custom-cdk-bootstrap/custom123/version",
      false,
      true);

    assertEquals("deploy-external-id", synthesizer.deployRoleExternalId());
    assertEquals("file-external-id", synthesizer.fileAssetPublishingExternalId());
    assertEquals("image-external-id", synthesizer.imageAssetPublishingExternalId());
    assertEquals("lookup-external-id", synthesizer.lookupRoleExternalId());
    assertFalse(synthesizer.generateBootstrapVersionRule());
    assertTrue(synthesizer.useLookupRoleForStackOperations());
  }

  @Test
  void testSynthesizerCustomQualifier() {
    var synthesizer = new Synthesizer(
      "arn:aws:iam::123456789012:role/exec",
      "arn:aws:iam::123456789012:role/deploy",
      "arn:aws:iam::123456789012:role/file",
      "arn:aws:iam::123456789012:role/image",
      "arn:aws:iam::123456789012:role/lookup",
      "myqualifier",
      "my-prefix-",
      null,
      "my-docker-",
      null,
      "my-bucket",
      null,
      "my-repo",
      null,
      "/my-bootstrap/myqualifier/version",
      true,
      true);

    assertEquals("myqualifier", synthesizer.qualifier());
    assertEquals("my-prefix-", synthesizer.bucketPrefix());
    assertEquals("my-docker-", synthesizer.dockerTagPrefix());
    assertEquals("my-bucket", synthesizer.fileAssetsBucketName());
    assertEquals("my-repo", synthesizer.imageAssetsRepositoryName());
  }

  @Test
  void testSynthesizerEquality() {
    var synth1 = new Synthesizer(
      "arn1", "arn2", "arn3", "arn4", "arn5",
      "qual", "prefix", null, "docker", null,
      "bucket", null, "repo", null, "/param", true, false);
    var synth2 = new Synthesizer(
      "arn1", "arn2", "arn3", "arn4", "arn5",
      "qual", "prefix", null, "docker", null,
      "bucket", null, "repo", null, "/param", true, false);

    assertRecordEquality(synth1, synth2);
  }
}
