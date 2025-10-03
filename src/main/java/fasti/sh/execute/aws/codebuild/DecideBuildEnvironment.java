package fasti.sh.execute.aws.codebuild;

import fasti.sh.execute.aws.s3.BucketConstruct;
import fasti.sh.model.aws.codebuild.Environment;
import fasti.sh.model.main.Common;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awscdk.services.codebuild.BuildEnvironment;
import software.amazon.awscdk.services.codebuild.BuildEnvironmentCertificate;
import software.amazon.awscdk.services.codebuild.IBuildImage;
import software.constructs.Construct;

@Slf4j
public class DecideBuildEnvironment {

  public static BuildEnvironment from(Construct scope, Common common, Environment e, IBuildImage buildImage) {
    log.debug("build environment configuration [common: {} environment: {}]", common, e);

    var environment = BuildEnvironment
      .builder()
      .environmentVariables(e.environmentVariables())
      .computeType(e.computeType())
      .buildImage(buildImage)
      .privileged(e.privileged());

    Optional
      .ofNullable(e.certificate().bucket())
      .ifPresent(
        b -> environment
          .certificate(
            BuildEnvironmentCertificate
              .builder()
              .bucket(new BucketConstruct(scope, common, b).bucket())
              .objectKey(e.certificate().objectKey())
              .build()));

    return environment.build();
  }
}
