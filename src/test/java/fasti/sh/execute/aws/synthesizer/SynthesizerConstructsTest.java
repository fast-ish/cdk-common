package fasti.sh.execute.aws.synthesizer;

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
 * Tests for CDK synthesizer nested stack constructs.
 */
class SynthesizerConstructsTest {

  private SynthesizerResources createTestSynthesizerResources() {
    var kms = new Kms(
      "synth-key",
      "Synthesizer encryption key",
      true,
      true,
      "encrypt_decrypt",
      "symmetric_default",
      "destroy");

    var assets = new S3Bucket(
      "synth-assets-bucket",
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
      Map.of("Purpose", "synthesizer-assets"));

    var encryption = new Encryption(false, null);
    var ecr = new EcrRepository(
      "synth-ecr-repo",
      true,
      true,
      TagMutability.IMMUTABLE,
      RemovalPolicy.DESTROY,
      encryption);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("cloudformation.amazonaws.com")
      .conditions(Map.of())
      .build();

    var cdkExec = new IamRole(
      "synth-exec-role",
      "Synthesizer exec role",
      principal,
      List.of("AdministratorAccess"),
      List.of(),
      Map.of("Role", "exec"));

    var cdkDeploy = new IamRole(
      "synth-deploy-role",
      "Synthesizer deploy role",
      principal,
      List.of("PowerUserAccess"),
      List.of(),
      Map.of("Role", "deploy"));

    var cdkLookup = new IamRole(
      "synth-lookup-role",
      "Synthesizer lookup role",
      principal,
      List.of("ReadOnlyAccess"),
      List.of(),
      Map.of("Role", "lookup"));

    var cdkAssets = new IamRole(
      "synth-assets-role",
      "Synthesizer assets role",
      principal,
      List.of("AmazonS3FullAccess"),
      List.of(),
      Map.of("Role", "assets"));

    var cdkImages = new IamRole(
      "synth-images-role",
      "Synthesizer images role",
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
  void testSynthesizerStorageNestedStack() {
    var ctx = createTestContext();
    var conf = createTestSynthesizerResources();
    var props = NestedStackProps
      .builder()
      .description("Synthesizer storage resources")
      .build();

    var construct = new SynthesizerStorageNestedStack(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.cdkEcr());
    assertNotNull(construct.cdkAssets());
  }

  @Test
  void testSynthesizerRolesNestedStack() {
    var ctx = createTestContext();
    var conf = createTestSynthesizerResources();
    var props = NestedStackProps
      .builder()
      .description("Synthesizer IAM roles")
      .build();

    var construct = new SynthesizerRolesNestedStack(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.cdkExec());
    assertNotNull(construct.cdkDeploy());
    assertNotNull(construct.cdkLookup());
    assertNotNull(construct.cdkAssets());
    assertNotNull(construct.cdkImages());
  }

  @Test
  void testSynthesizerNestedStack() {
    var ctx = createTestContext();
    var conf = createTestSynthesizerResources();
    var props = NestedStackProps
      .builder()
      .description("Complete synthesizer stack")
      .build();

    var construct = new SynthesizerNestedStack(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.key());
    assertNotNull(construct.version());
    assertNotNull(construct.roles());
    assertNotNull(construct.storage());
  }

  @Test
  void testSynthesizerStorageWithRetainPolicy() {
    var ctx = createTestContext();

    var kms = new Kms(
      "retained-key",
      "Retained encryption key",
      true,
      false,
      "encrypt_decrypt",
      "symmetric_default",
      "retain");

    var assets = new S3Bucket(
      "retained-assets",
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      true,
      false, // cannot use autoDeleteObjects with RETAIN policy
      false,
      RemovalPolicy.RETAIN,
      null,
      Map.of());

    var encryption = new Encryption(true, kms);
    var ecr = new EcrRepository(
      "retained-ecr",
      true,
      false, // cannot use emptyOnDelete with RETAIN policy
      TagMutability.IMMUTABLE,
      RemovalPolicy.RETAIN,
      encryption);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("cloudformation.amazonaws.com")
      .conditions(Map.of())
      .build();

    var cdkExec = new IamRole("exec", "Exec", principal, List.of(), List.of(), Map.of());
    var cdkDeploy = new IamRole("deploy", "Deploy", principal, List.of(), List.of(), Map.of());
    var cdkLookup = new IamRole("lookup", "Lookup", principal, List.of(), List.of(), Map.of());
    var cdkAssets = new IamRole("assets", "Assets", principal, List.of(), List.of(), Map.of());
    var cdkImages = new IamRole("images", "Images", principal, List.of(), List.of(), Map.of());

    var conf = new SynthesizerResources(kms, assets, ecr, cdkExec, cdkDeploy, cdkLookup, cdkAssets, cdkImages);
    var props = NestedStackProps.builder().build();

    var construct = new SynthesizerStorageNestedStack(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.cdkEcr());
    assertNotNull(construct.cdkAssets());
  }

  @Test
  void testSynthesizerStorageWithMutableTags() {
    var ctx = createTestContext();

    var kms = new Kms("key", "Key", true, true, "encrypt_decrypt", "symmetric_default", "destroy");
    var assets = new S3Bucket(
      "assets",
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      false,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var encryption = new Encryption(false, null);
    var ecr = new EcrRepository(
      "mutable-ecr",
      true,
      false,
      TagMutability.MUTABLE,
      RemovalPolicy.DESTROY,
      encryption);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("cloudformation.amazonaws.com")
      .conditions(Map.of())
      .build();

    var cdkExec = new IamRole("exec", "Exec", principal, List.of(), List.of(), Map.of());
    var cdkDeploy = new IamRole("deploy", "Deploy", principal, List.of(), List.of(), Map.of());
    var cdkLookup = new IamRole("lookup", "Lookup", principal, List.of(), List.of(), Map.of());
    var cdkAssets = new IamRole("assets", "Assets", principal, List.of(), List.of(), Map.of());
    var cdkImages = new IamRole("images", "Images", principal, List.of(), List.of(), Map.of());

    var conf = new SynthesizerResources(kms, assets, ecr, cdkExec, cdkDeploy, cdkLookup, cdkAssets, cdkImages);
    var props = NestedStackProps.builder().build();

    var construct = new SynthesizerStorageNestedStack(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.cdkEcr());
  }

  @Test
  void testSynthesizerRolesWithMultiplePolicies() {
    var ctx = createTestContext();

    var kms = new Kms("key", "Key", true, true, "encrypt_decrypt", "symmetric_default", "destroy");
    var assets = new S3Bucket(
      "assets",
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      false,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var encryption = new Encryption(false, null);
    var ecr = new EcrRepository("ecr", true, true, TagMutability.IMMUTABLE, RemovalPolicy.DESTROY, encryption);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("cloudformation.amazonaws.com")
      .conditions(Map.of())
      .build();

    var cdkExec = new IamRole(
      "exec-multi",
      "Exec with multiple policies",
      principal,
      List.of("AdministratorAccess", "ReadOnlyAccess"),
      List.of(),
      Map.of());

    var cdkDeploy = new IamRole(
      "deploy-multi",
      "Deploy with multiple policies",
      principal,
      List.of("PowerUserAccess", "IAMReadOnlyAccess"),
      List.of(),
      Map.of());

    var cdkLookup = new IamRole("lookup", "Lookup", principal, List.of("ReadOnlyAccess"), List.of(), Map.of());
    var cdkAssets = new IamRole("assets", "Assets", principal, List.of("AmazonS3FullAccess"), List.of(), Map.of());
    var cdkImages = new IamRole("images", "Images", principal, List.of("AmazonEC2ContainerRegistryFullAccess"), List.of(), Map.of());

    var conf = new SynthesizerResources(kms, assets, ecr, cdkExec, cdkDeploy, cdkLookup, cdkAssets, cdkImages);
    var props = NestedStackProps.builder().build();

    var construct = new SynthesizerRolesNestedStack(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.cdkExec());
    assertNotNull(construct.cdkDeploy());
  }

  @Test
  void testSynthesizerStorageWithVersioning() {
    var ctx = createTestContext();

    var kms = new Kms("key", "Key", true, true, "encrypt_decrypt", "symmetric_default", "destroy");
    var assets = new S3Bucket(
      "versioned-assets",
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      true,
      true,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of("Versioning", "enabled"));

    var encryption = new Encryption(true, kms);
    var ecr = new EcrRepository("ecr", true, true, TagMutability.IMMUTABLE, RemovalPolicy.DESTROY, encryption);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("cloudformation.amazonaws.com")
      .conditions(Map.of())
      .build();

    var cdkExec = new IamRole("exec", "Exec", principal, List.of(), List.of(), Map.of());
    var cdkDeploy = new IamRole("deploy", "Deploy", principal, List.of(), List.of(), Map.of());
    var cdkLookup = new IamRole("lookup", "Lookup", principal, List.of(), List.of(), Map.of());
    var cdkAssets = new IamRole("assets", "Assets", principal, List.of(), List.of(), Map.of());
    var cdkImages = new IamRole("images", "Images", principal, List.of(), List.of(), Map.of());

    var conf = new SynthesizerResources(kms, assets, ecr, cdkExec, cdkDeploy, cdkLookup, cdkAssets, cdkImages);
    var props = NestedStackProps.builder().build();

    var construct = new SynthesizerStorageNestedStack(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.cdkAssets());
  }

  @Test
  void testSynthesizerWithEcrScanning() {
    var ctx = createTestContext();

    var kms = new Kms("key", "Key", false, false, "encrypt_decrypt", "symmetric_default", "destroy");
    var assets = new S3Bucket(
      "assets",
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      false,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var encryption = new Encryption(false, null);
    var ecr = new EcrRepository(
      "scanning-ecr",
      true,
      true,
      TagMutability.IMMUTABLE,
      RemovalPolicy.DESTROY,
      encryption);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("cloudformation.amazonaws.com")
      .conditions(Map.of())
      .build();

    var cdkExec = new IamRole("exec", "Exec", principal, List.of(), List.of(), Map.of());
    var cdkDeploy = new IamRole("deploy", "Deploy", principal, List.of(), List.of(), Map.of());
    var cdkLookup = new IamRole("lookup", "Lookup", principal, List.of(), List.of(), Map.of());
    var cdkAssets = new IamRole("assets", "Assets", principal, List.of(), List.of(), Map.of());
    var cdkImages = new IamRole("images", "Images", principal, List.of(), List.of(), Map.of());

    var conf = new SynthesizerResources(kms, assets, ecr, cdkExec, cdkDeploy, cdkLookup, cdkAssets, cdkImages);
    var props = NestedStackProps.builder().description("With scanning").build();

    var construct = new SynthesizerStorageNestedStack(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.cdkEcr());
  }

  @Test
  void testSynthesizerNestedStackMinimalConfig() {
    var ctx = createTestContext();

    var kms = new Kms("minimal-key", "Key", false, false, "encrypt_decrypt", "symmetric_default", "destroy");
    var assets = new S3Bucket(
      "minimal-assets",
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      false,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var encryption = new Encryption(false, null);
    var ecr = new EcrRepository("minimal-ecr", false, false, TagMutability.MUTABLE, RemovalPolicy.DESTROY, encryption);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("cloudformation.amazonaws.com")
      .conditions(Map.of())
      .build();

    var cdkExec = new IamRole("exec", "Exec", principal, List.of(), List.of(), Map.of());
    var cdkDeploy = new IamRole("deploy", "Deploy", principal, List.of(), List.of(), Map.of());
    var cdkLookup = new IamRole("lookup", "Lookup", principal, List.of(), List.of(), Map.of());
    var cdkAssets = new IamRole("assets", "Assets", principal, List.of(), List.of(), Map.of());
    var cdkImages = new IamRole("images", "Images", principal, List.of(), List.of(), Map.of());

    var conf = new SynthesizerResources(kms, assets, ecr, cdkExec, cdkDeploy, cdkLookup, cdkAssets, cdkImages);
    var props = NestedStackProps.builder().build();

    var construct = new SynthesizerNestedStack(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.storage());
  }

  @Test
  void testSynthesizerRolesWithFederatedPrincipal() {
    var ctx = createTestContext();

    var kms = new Kms("key", "Key", true, true, "encrypt_decrypt", "symmetric_default", "destroy");
    var assets = new S3Bucket(
      "assets",
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      false,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var encryption = new Encryption(false, null);
    var ecr = new EcrRepository("ecr", true, true, TagMutability.IMMUTABLE, RemovalPolicy.DESTROY, encryption);

    var principal = Principal
      .builder()
      .type(PrincipalType.FEDERATED)
      .value("arn:aws:iam::123456789012:saml-provider/ExampleProvider")
      .conditions(Map.of("StringEquals", Map.of("SAML:aud", "https://signin.aws.amazon.com/saml")))
      .build();

    var cdkExec = new IamRole("fed-exec", "Federated exec", principal, List.of(), List.of(), Map.of());
    var cdkDeploy = new IamRole("fed-deploy", "Federated deploy", principal, List.of(), List.of(), Map.of());
    var cdkLookup = new IamRole("fed-lookup", "Federated lookup", principal, List.of(), List.of(), Map.of());
    var cdkAssets = new IamRole("fed-assets", "Federated assets", principal, List.of(), List.of(), Map.of());
    var cdkImages = new IamRole("fed-images", "Federated images", principal, List.of(), List.of(), Map.of());

    var conf = new SynthesizerResources(kms, assets, ecr, cdkExec, cdkDeploy, cdkLookup, cdkAssets, cdkImages);
    var props = NestedStackProps.builder().build();

    var construct = new SynthesizerRolesNestedStack(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.cdkExec());
  }

  @Test
  void testSynthesizerStorageWithPublicAccess() {
    var ctx = createTestContext();

    var kms = new Kms("key", "Key", true, true, "encrypt_decrypt", "symmetric_default", "destroy");
    var assets = new S3Bucket(
      "public-assets",
      null,
      BucketAccessControl.PUBLIC_READ,
      ObjectOwnership.BUCKET_OWNER_PREFERRED,
      List.of(),
      List.of(),
      false,
      false,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var encryption = new Encryption(false, null);
    var ecr = new EcrRepository("ecr", true, true, TagMutability.IMMUTABLE, RemovalPolicy.DESTROY, encryption);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("cloudformation.amazonaws.com")
      .conditions(Map.of())
      .build();

    var cdkExec = new IamRole("exec", "Exec", principal, List.of(), List.of(), Map.of());
    var cdkDeploy = new IamRole("deploy", "Deploy", principal, List.of(), List.of(), Map.of());
    var cdkLookup = new IamRole("lookup", "Lookup", principal, List.of(), List.of(), Map.of());
    var cdkAssets = new IamRole("assets", "Assets", principal, List.of(), List.of(), Map.of());
    var cdkImages = new IamRole("images", "Images", principal, List.of(), List.of(), Map.of());

    var conf = new SynthesizerResources(kms, assets, ecr, cdkExec, cdkDeploy, cdkLookup, cdkAssets, cdkImages);
    var props = NestedStackProps.builder().build();

    var construct = new SynthesizerStorageNestedStack(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.cdkAssets());
  }

  @Test
  void testSynthesizerNestedStackWithCustomTags() {
    var ctx = createTestContext();

    var kms = new Kms("key", "Key", true, true, "encrypt_decrypt", "symmetric_default", "destroy");
    var assets = new S3Bucket(
      "tagged-assets",
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      false,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of("Environment", "production", "CostCenter", "engineering"));

    var encryption = new Encryption(false, null);
    var ecr = new EcrRepository("ecr", true, true, TagMutability.IMMUTABLE, RemovalPolicy.DESTROY, encryption);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("cloudformation.amazonaws.com")
      .conditions(Map.of())
      .build();

    var cdkExec = new IamRole("exec", "Exec", principal, List.of(), List.of(), Map.of("Role", "exec"));
    var cdkDeploy = new IamRole("deploy", "Deploy", principal, List.of(), List.of(), Map.of("Role", "deploy"));
    var cdkLookup = new IamRole("lookup", "Lookup", principal, List.of(), List.of(), Map.of("Role", "lookup"));
    var cdkAssets = new IamRole("assets", "Assets", principal, List.of(), List.of(), Map.of("Role", "assets"));
    var cdkImages = new IamRole("images", "Images", principal, List.of(), List.of(), Map.of("Role", "images"));

    var conf = new SynthesizerResources(kms, assets, ecr, cdkExec, cdkDeploy, cdkLookup, cdkAssets, cdkImages);
    var props = NestedStackProps.builder().description("With custom tags").build();

    var construct = new SynthesizerNestedStack(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.storage());
    assertNotNull(construct.roles());
  }

  @Test
  void testSynthesizerStorageWithAsymmetricKey() {
    var ctx = createTestContext();

    var kms = new Kms(
      "asymmetric-key",
      "Asymmetric encryption key",
      true,
      false, // asymmetric keys cannot have rotation enabled
      "sign_verify",
      "rsa_2048",
      "destroy");

    var assets = new S3Bucket(
      "assets",
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      false,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var encryption = new Encryption(true, kms);
    var ecr = new EcrRepository("ecr", true, true, TagMutability.IMMUTABLE, RemovalPolicy.DESTROY, encryption);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("cloudformation.amazonaws.com")
      .conditions(Map.of())
      .build();

    var cdkExec = new IamRole("exec", "Exec", principal, List.of(), List.of(), Map.of());
    var cdkDeploy = new IamRole("deploy", "Deploy", principal, List.of(), List.of(), Map.of());
    var cdkLookup = new IamRole("lookup", "Lookup", principal, List.of(), List.of(), Map.of());
    var cdkAssets = new IamRole("assets", "Assets", principal, List.of(), List.of(), Map.of());
    var cdkImages = new IamRole("images", "Images", principal, List.of(), List.of(), Map.of());

    var conf = new SynthesizerResources(kms, assets, ecr, cdkExec, cdkDeploy, cdkLookup, cdkAssets, cdkImages);
    var props = NestedStackProps.builder().build();

    var construct = new SynthesizerStorageNestedStack(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.cdkEcr());
  }

  @Test
  void testSynthesizerRolesWithAccountPrincipal() {
    var ctx = createTestContext();

    var kms = new Kms("key", "Key", true, true, "encrypt_decrypt", "symmetric_default", "destroy");
    var assets = new S3Bucket(
      "assets",
      null,
      BucketAccessControl.PRIVATE,
      ObjectOwnership.BUCKET_OWNER_ENFORCED,
      List.of(),
      List.of(),
      false,
      false,
      false,
      RemovalPolicy.DESTROY,
      null,
      Map.of());

    var encryption = new Encryption(false, null);
    var ecr = new EcrRepository("ecr", true, true, TagMutability.IMMUTABLE, RemovalPolicy.DESTROY, encryption);

    var principal = Principal
      .builder()
      .type(PrincipalType.AWS)
      .value("arn:aws:iam::123456789012:root")
      .conditions(Map.of())
      .build();

    var cdkExec = new IamRole("acct-exec", "Account exec", principal, List.of(), List.of(), Map.of());
    var cdkDeploy = new IamRole("acct-deploy", "Account deploy", principal, List.of(), List.of(), Map.of());
    var cdkLookup = new IamRole("acct-lookup", "Account lookup", principal, List.of(), List.of(), Map.of());
    var cdkAssets = new IamRole("acct-assets", "Account assets", principal, List.of(), List.of(), Map.of());
    var cdkImages = new IamRole("acct-images", "Account images", principal, List.of(), List.of(), Map.of());

    var conf = new SynthesizerResources(kms, assets, ecr, cdkExec, cdkDeploy, cdkLookup, cdkAssets, cdkImages);
    var props = NestedStackProps.builder().build();

    var construct = new SynthesizerRolesNestedStack(ctx.scope(), ctx.common(), conf, props);

    assertNotNull(construct);
    assertNotNull(construct.cdkLookup());
    assertNotNull(construct.cdkAssets());
    assertNotNull(construct.cdkImages());
  }
}
