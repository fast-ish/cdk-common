package fasti.sh.model.aws.codebuild;

import static java.util.Map.Entry;
import static java.util.Map.entry;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import lombok.Builder;
import software.amazon.awscdk.services.codebuild.BuildEnvironmentVariable;
import software.amazon.awscdk.services.codebuild.BuildEnvironmentVariableType;
import software.amazon.awscdk.services.codebuild.ComputeType;

/**
 * Configuration record for AWS CodeBuild environment settings.
 *
 * <p>
 * Defines the build environment including compute resources, environment variables, privileges, and certificates required for the build
 * process.
 *
 * @param computeType
 *          The compute type for the build environment (e.g., BUILD_GENERAL1_SMALL)
 * @param variables
 *          Map of environment variables available during the build
 * @param privileged
 *          Whether the build environment should run in privileged mode
 * @param certificate
 *          SSL certificate configuration for secure builds
 */
@Builder
public record Environment(
  ComputeType computeType,
  Map<String, String> variables,
  boolean privileged,
  Certificate certificate
) {

  /**
   * Converts the environment variables map to AWS CDK BuildEnvironmentVariable objects.
   *
   * <p>
   * Transforms string-based environment variables into CDK-compatible BuildEnvironmentVariable objects with PLAINTEXT type for use in
   * CodeBuild environments.
   *
   * @return Map of environment variable names to BuildEnvironmentVariable objects
   */
  public Map<String, BuildEnvironmentVariable> environmentVariables() {
    return this
      .variables()
      .entrySet()
      .stream()
      .map(
        kv -> entry(
          kv.getKey(),
          BuildEnvironmentVariable.builder().type(BuildEnvironmentVariableType.PLAINTEXT).value(kv.getValue()).build()))
      .collect(toMap(Entry::getKey, Entry::getValue));
  }
}
