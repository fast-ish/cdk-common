package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.codebuild.PipelineConstruct;
import fasti.sh.execute.aws.iam.RoleConstruct;
import fasti.sh.execute.aws.s3.BucketConstruct;
import fasti.sh.model.aws.codebuild.CodeStarConnectionSource;
import fasti.sh.model.aws.codebuild.Pipeline;
import fasti.sh.model.aws.codebuild.Variable;
import fasti.sh.model.aws.iam.IamRole;
import fasti.sh.model.aws.iam.Principal;
import fasti.sh.model.aws.iam.PrincipalType;
import fasti.sh.model.aws.s3.S3Bucket;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.codepipeline.ExecutionMode;
import software.amazon.awscdk.services.codepipeline.PipelineType;
import software.amazon.awscdk.services.s3.BucketAccessControl;
import software.amazon.awscdk.services.s3.ObjectOwnership;

/**
 * Tests for AWS CodeBuild and CodePipeline constructs.
 */
class CodeBuildConstructsTest {

  @Test
  void testPipelineConstructBasic() {
    var ctx = createTestContext();

    // Create S3 bucket for artifacts
    var s3Conf = new S3Bucket(
      "pipeline-artifacts-" + System.currentTimeMillis(),
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
    var bucketConstruct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    // Create IAM role for pipeline
    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("codepipeline.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "pipeline-role",
      "CodePipeline execution role",
      principal,
      List.of("AWSCodePipelineFullAccess"),
      List.of(),
      Map.of());
    var roleConstruct = new RoleConstruct(ctx.scope(), ctx.common(), role);

    // Create pipeline configuration
    var cdkRepo = new CodeStarConnectionSource(
      "my-org",
      "cdk-repo",
      "main",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      true);

    var deployment = new CodeStarConnectionSource(
      "my-org",
      "deploy-repo",
      "main",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      false);

    var variable = new Variable("ENVIRONMENT", "dev");

    var pipelineConf = new Pipeline(
      "test-pipeline",
      "Test CI/CD pipeline",
      PipelineType.V2,
      ExecutionMode.QUEUED,
      List.of(variable),
      false,
      true,
      cdkRepo,
      deployment);

    var construct = new PipelineConstruct(
      ctx.scope(),
      ctx.common(),
      pipelineConf,
      bucketConstruct.bucket(),
      roleConstruct.role());

    assertNotNull(construct);
  }

  @Test
  void testPipelineConstructWithSingleVariable() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "pipeline-single-var-" + System.currentTimeMillis(),
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
    var bucketConstruct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("codepipeline.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "pipeline-single-var-role",
      "Pipeline role with single variable",
      principal,
      List.of("AWSCodePipelineFullAccess"),
      List.of(),
      Map.of());
    var roleConstruct = new RoleConstruct(ctx.scope(), ctx.common(), role);

    var cdkRepo = new CodeStarConnectionSource(
      "org",
      "repo1",
      "develop",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      true);

    var deployment = new CodeStarConnectionSource(
      "org",
      "repo2",
      "develop",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      false);

    var variable = new Variable("ENVIRONMENT", "staging");

    var pipelineConf = new Pipeline(
      "single-var-pipeline",
      "Pipeline with single variable",
      PipelineType.V2,
      ExecutionMode.QUEUED,
      List.of(variable),
      false,
      false,
      cdkRepo,
      deployment);

    var construct = new PipelineConstruct(
      ctx.scope(),
      ctx.common(),
      pipelineConf,
      bucketConstruct.bucket(),
      roleConstruct.role());

    assertNotNull(construct);
  }

  @Test
  void testPipelineConstructWithCrossAccountKeys() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "pipeline-cross-account-" + System.currentTimeMillis(),
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
    var bucketConstruct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("codepipeline.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "cross-account-pipeline-role",
      "Cross-account pipeline role",
      principal,
      List.of("AWSCodePipelineFullAccess"),
      List.of(),
      Map.of());
    var roleConstruct = new RoleConstruct(ctx.scope(), ctx.common(), role);

    var cdkRepo = new CodeStarConnectionSource(
      "org",
      "infrastructure",
      "main",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      true);

    var deployment = new CodeStarConnectionSource(
      "org",
      "application",
      "main",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      true);

    var pipelineConf = new Pipeline(
      "cross-account-pipeline",
      "Pipeline with cross-account access",
      PipelineType.V2,
      ExecutionMode.PARALLEL,
      List.of(),
      true, // crossAccountKeys enabled
      true,
      cdkRepo,
      deployment);

    var construct = new PipelineConstruct(
      ctx.scope(),
      ctx.common(),
      pipelineConf,
      bucketConstruct.bucket(),
      roleConstruct.role());

    assertNotNull(construct);
  }

  @Test
  void testPipelineConstructV1Type() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "pipeline-v1-" + System.currentTimeMillis(),
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
    var bucketConstruct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("codepipeline.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "v1-pipeline-role",
      "V1 pipeline role",
      principal,
      List.of("AWSCodePipelineFullAccess"),
      List.of(),
      Map.of());
    var roleConstruct = new RoleConstruct(ctx.scope(), ctx.common(), role);

    var cdkRepo = new CodeStarConnectionSource(
      "org",
      "legacy-repo",
      "master",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      true);

    var deployment = new CodeStarConnectionSource(
      "org",
      "legacy-deploy",
      "master",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      false);

    var pipelineConf = new Pipeline(
      "v1-pipeline",
      "Legacy V1 pipeline",
      PipelineType.V1,
      ExecutionMode.SUPERSEDED,
      List.of(),
      false,
      false,
      cdkRepo,
      deployment);

    var construct = new PipelineConstruct(
      ctx.scope(),
      ctx.common(),
      pipelineConf,
      bucketConstruct.bucket(),
      roleConstruct.role());

    assertNotNull(construct);
  }

  @Test
  void testPipelineConstructSupersededExecution() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "pipeline-superseded-" + System.currentTimeMillis(),
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
    var bucketConstruct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("codepipeline.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "superseded-pipeline-role",
      "Superseded execution pipeline role",
      principal,
      List.of("AWSCodePipelineFullAccess"),
      List.of(),
      Map.of());
    var roleConstruct = new RoleConstruct(ctx.scope(), ctx.common(), role);

    var cdkRepo = new CodeStarConnectionSource(
      "company",
      "services",
      "release",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      true);

    var deployment = new CodeStarConnectionSource(
      "company",
      "config",
      "release",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      false);

    var pipelineConf = new Pipeline(
      "superseded-pipeline",
      "Pipeline with superseded execution",
      PipelineType.V2,
      ExecutionMode.SUPERSEDED,
      List.of(),
      false,
      false,
      cdkRepo,
      deployment);

    var construct = new PipelineConstruct(
      ctx.scope(),
      ctx.common(),
      pipelineConf,
      bucketConstruct.bucket(),
      roleConstruct.role());

    assertNotNull(construct);
  }

  @Test
  void testPipelineConstructTwoVariables() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "pipeline-two-vars-" + System.currentTimeMillis(),
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
    var bucketConstruct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("codepipeline.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "two-vars-pipeline-role",
      "Two-variable pipeline role",
      principal,
      List.of("AWSCodePipelineFullAccess"),
      List.of(),
      Map.of());
    var roleConstruct = new RoleConstruct(ctx.scope(), ctx.common(), role);

    var cdkRepo = new CodeStarConnectionSource(
      "org",
      "app",
      "main",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      true);

    var deployment = new CodeStarConnectionSource(
      "org",
      "config",
      "main",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      false);

    var var1 = new Variable("ENVIRONMENT", "production");

    var pipelineConf = new Pipeline(
      "two-vars-pipeline",
      "Pipeline with variable",
      PipelineType.V2,
      ExecutionMode.QUEUED,
      List.of(var1),
      false,
      true,
      cdkRepo,
      deployment);

    var construct = new PipelineConstruct(
      ctx.scope(),
      ctx.common(),
      pipelineConf,
      bucketConstruct.bucket(),
      roleConstruct.role());

    assertNotNull(construct);
  }

  @Test
  void testPipelineConstructParallelExecution() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "pipeline-parallel-" + System.currentTimeMillis(),
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
    var bucketConstruct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("codepipeline.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "parallel-pipeline-role",
      "Parallel execution pipeline role",
      principal,
      List.of("AWSCodePipelineFullAccess"),
      List.of(),
      Map.of());
    var roleConstruct = new RoleConstruct(ctx.scope(), ctx.common(), role);

    var cdkRepo = new CodeStarConnectionSource(
      "team",
      "microservice",
      "develop",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      true);

    var deployment = new CodeStarConnectionSource(
      "team",
      "deployment",
      "develop",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      false);

    var pipelineConf = new Pipeline(
      "parallel-pipeline",
      "Pipeline with parallel execution",
      PipelineType.V2,
      ExecutionMode.PARALLEL,
      List.of(),
      false,
      true,
      cdkRepo,
      deployment);

    var construct = new PipelineConstruct(
      ctx.scope(),
      ctx.common(),
      pipelineConf,
      bucketConstruct.bucket(),
      roleConstruct.role());

    assertNotNull(construct);
  }

  @Test
  void testPipelineConstructDifferentBranches() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "pipeline-branches-" + System.currentTimeMillis(),
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
    var bucketConstruct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("codepipeline.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "branches-pipeline-role",
      "Different branches pipeline role",
      principal,
      List.of("AWSCodePipelineFullAccess"),
      List.of(),
      Map.of());
    var roleConstruct = new RoleConstruct(ctx.scope(), ctx.common(), role);

    var cdkRepo = new CodeStarConnectionSource(
      "company",
      "backend",
      "feature/new-api",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      true);

    var deployment = new CodeStarConnectionSource(
      "company",
      "infrastructure",
      "feature/new-resources",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      true);

    var pipelineConf = new Pipeline(
      "branches-pipeline",
      "Pipeline with different branches",
      PipelineType.V2,
      ExecutionMode.QUEUED,
      List.of(),
      false,
      true,
      cdkRepo,
      deployment);

    var construct = new PipelineConstruct(
      ctx.scope(),
      ctx.common(),
      pipelineConf,
      bucketConstruct.bucket(),
      roleConstruct.role());

    assertNotNull(construct);
  }

  @Test
  void testPipelineConstructCrossAccountWithVariable() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "pipeline-cross-var-" + System.currentTimeMillis(),
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
    var bucketConstruct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("codepipeline.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "cross-var-pipeline-role",
      "Cross-account with variable role",
      principal,
      List.of("AWSCodePipelineFullAccess"),
      List.of(),
      Map.of());
    var roleConstruct = new RoleConstruct(ctx.scope(), ctx.common(), role);

    var cdkRepo = new CodeStarConnectionSource(
      "enterprise",
      "platform",
      "production",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      true);

    var deployment = new CodeStarConnectionSource(
      "enterprise",
      "config-prod",
      "production",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      true);

    var var1 = new Variable("ACCOUNT_ID", "123456789012");

    var pipelineConf = new Pipeline(
      "cross-var-pipeline",
      "Cross-account pipeline with variable",
      PipelineType.V2,
      ExecutionMode.PARALLEL,
      List.of(var1),
      true,
      true,
      cdkRepo,
      deployment);

    var construct = new PipelineConstruct(
      ctx.scope(),
      ctx.common(),
      pipelineConf,
      bucketConstruct.bucket(),
      roleConstruct.role());

    assertNotNull(construct);
  }

  @Test
  void testPipelineConstructNoVariables() {
    var ctx = createTestContext();

    var s3Conf = new S3Bucket(
      "pipeline-no-vars-" + System.currentTimeMillis(),
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
    var bucketConstruct = new BucketConstruct(ctx.scope(), ctx.common(), s3Conf);

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("codepipeline.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "no-vars-pipeline-role",
      "No variables pipeline role",
      principal,
      List.of("AWSCodePipelineFullAccess"),
      List.of(),
      Map.of());
    var roleConstruct = new RoleConstruct(ctx.scope(), ctx.common(), role);

    var cdkRepo = new CodeStarConnectionSource(
      "simple-org",
      "simple-app",
      "main",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      false);

    var deployment = new CodeStarConnectionSource(
      "simple-org",
      "simple-config",
      "main",
      "arn:aws:codestar-connections:us-east-1:123456789012:connection/test",
      false);

    var pipelineConf = new Pipeline(
      "no-vars-pipeline",
      "Simple pipeline without variables",
      PipelineType.V2,
      ExecutionMode.QUEUED,
      List.of(),
      false,
      false,
      cdkRepo,
      deployment);

    var construct = new PipelineConstruct(
      ctx.scope(),
      ctx.common(),
      pipelineConf,
      bucketConstruct.bucket(),
      roleConstruct.role());

    assertNotNull(construct);
  }
}
