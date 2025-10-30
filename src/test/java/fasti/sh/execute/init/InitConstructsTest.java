package fasti.sh.execute.init;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.ecr.EcrRepository;
import fasti.sh.model.aws.ecr.Encryption;
import fasti.sh.model.aws.iam.IamRole;
import fasti.sh.model.aws.iam.Principal;
import fasti.sh.model.aws.iam.PrincipalType;
import fasti.sh.model.aws.kms.Kms;
import fasti.sh.model.aws.s3.S3Bucket;
import fasti.sh.model.main.SynthesizerResources;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.NestedStackProps;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.ecr.TagMutability;
import software.amazon.awscdk.services.s3.BucketAccessControl;
import software.amazon.awscdk.services.s3.ObjectOwnership;

/**
 * Tests for CDK synthesizer initialization constructs.
 */
class InitConstructsTest {

  private SynthesizerResources createTestSynthesizerResources() {
    var kms = new Kms(
      "cdk-key",
      "CDK encryption key",
      true,
      true,
      "encrypt_decrypt",
      "symmetric_default",
      "retain");

    var assets = new S3Bucket(
      "cdk-assets-bucket",
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      false, // autoDeleteObjects must be false when RemovalPolicy is RETAIN
      true,
      RemovalPolicy.RETAIN,
      null,
      Map.of("Purpose", "cdk-assets"));

    var encryption = new Encryption(false, null);
    var ecr = new EcrRepository(
      "cdk-ecr-repo",
      true,
      false, // emptyOnDelete must be false when RemovalPolicy is RETAIN
      TagMutability.IMMUTABLE,
      RemovalPolicy.RETAIN,
      encryption);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("cloudformation.amazonaws.com")
      .conditions(Map.of())
      .build();

    var cdkExec = new IamRole(
      "cdk-exec-role",
      "CDK execution role",
      principal,
      List.of("AdministratorAccess"),
      List.of(),
      Map.of("Role", "exec"));

    var cdkDeploy = new IamRole(
      "cdk-deploy-role",
      "CDK deployment role",
      principal,
      List.of("PowerUserAccess"),
      List.of(),
      Map.of("Role", "deploy"));

    var cdkLookup = new IamRole(
      "cdk-lookup-role",
      "CDK lookup role",
      principal,
      List.of("ReadOnlyAccess"),
      List.of(),
      Map.of("Role", "lookup"));

    var cdkAssets = new IamRole(
      "cdk-assets-role",
      "CDK assets publishing role",
      principal,
      List.of("AmazonS3FullAccess"),
      List.of(),
      Map.of("Role", "assets"));

    var cdkImages = new IamRole(
      "cdk-images-role",
      "CDK images publishing role",
      principal,
      List.of("AmazonEC2ContainerRegistryPowerUser"),
      List.of(),
      Map.of("Role", "images"));

    return new SynthesizerResources(
      kms,
      assets,
      ecr,
      cdkExec,
      cdkDeploy,
      cdkLookup,
      cdkAssets,
      cdkImages);
  }

  @Test
  void testSystemStorageConstruct() {
    var ctx = createTestContext();
    var conf = createTestSynthesizerResources();
    var props = NestedStackProps
      .builder()
      .description("CDK synthesizer storage")
      .build();

    var construct = new SystemStorageConstruct(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.cdkEcr());
    assertNotNull(construct.cdkAssets());
  }

  @Test
  void testSystemRolesConstruct() {
    var ctx = createTestContext();
    var conf = createTestSynthesizerResources();
    var props = NestedStackProps
      .builder()
      .description("CDK synthesizer roles")
      .build();

    var construct = new SystemRolesConstruct(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.cdkExec());
    assertNotNull(construct.cdkDeploy());
    assertNotNull(construct.cdkLookup());
    assertNotNull(construct.cdkAssets());
    assertNotNull(construct.cdkImages());
  }

  @Test
  void testSynthesizerConstruct() {
    var ctx = createTestContext();
    var conf = createTestSynthesizerResources();
    var props = NestedStackProps
      .builder()
      .description("CDK synthesizer")
      .build();

    var construct = new SynthesizerConstruct(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.key());
    assertNotNull(construct.version());
    assertNotNull(construct.roles());
    assertNotNull(construct.storage());
  }

  @Test
  void testSynthesizerConstructMinimalConfig() {
    var ctx = createTestContext();

    var kms = new Kms(
      "minimal-key",
      "Minimal CDK key",
      false,
      false,
      "encrypt_decrypt",
      "symmetric_default",
      "destroy");

    var assets = new S3Bucket(
      "minimal-assets",
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var encryption2 = new Encryption(false, null);
    var ecr = new EcrRepository(
      "minimal-ecr",
      false,
      false,
      TagMutability.MUTABLE,
      RemovalPolicy.DESTROY,
      encryption2);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("cloudformation.amazonaws.com")
      .conditions(Map.of())
      .build();

    var execRole = new IamRole("exec-role", "Exec role", principal, List.of(), List.of(), Map.of());
    var deployRole = new IamRole("deploy-role", "Deploy role", principal, List.of(), List.of(), Map.of());
    var lookupRole = new IamRole("lookup-role", "Lookup role", principal, List.of(), List.of(), Map.of());
    var assetsRole = new IamRole("assets-role", "Assets role", principal, List.of(), List.of(), Map.of());
    var imagesRole = new IamRole("images-role", "Images role", principal, List.of(), List.of(), Map.of());

    var conf = new SynthesizerResources(kms, assets, ecr, execRole, deployRole, lookupRole, assetsRole, imagesRole);
    var props = NestedStackProps.builder().build();

    var construct = new SynthesizerConstruct(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.key());
    assertNotNull(construct.version());
  }
}
