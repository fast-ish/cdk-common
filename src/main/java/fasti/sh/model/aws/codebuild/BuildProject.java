package fasti.sh.model.aws.codebuild;

/**
 * Configuration record for AWS CodeBuild project settings.
 *
 * <p>
 * Defines a complete build project configuration including environment settings, build specifications, logging, and caching options.
 *
 * @param badge
 *          Whether to enable build badge generation
 * @param name
 *          Unique identifier for the build project
 * @param description
 *          Human-readable description of the build project
 * @param buildspec
 *          Build specification file path or inline specification
 * @param concurrentBuildLimit
 *          Maximum number of concurrent builds allowed
 * @param environment
 *          Build environment configuration including compute and variables
 * @param logging
 *          CloudWatch logging configuration for build outputs
 * @param cache
 *          Whether to enable build caching for faster subsequent builds
 */
public record BuildProject(
  boolean badge,
  String name,
  String description,
  String buildspec,
  int concurrentBuildLimit,
  Environment environment,
  Logging logging,
  boolean cache
) {}
