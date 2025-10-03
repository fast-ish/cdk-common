package fasti.sh.model.aws.codebuild;

import fasti.sh.model.aws.cloudwatch.LogGroupConf;

/**
 * Configuration record for CodeBuild logging settings.
 *
 * <p>
 * Defines CloudWatch logging configuration for build processes including log group settings, prefixes, and enablement flags.
 *
 * @param logGroup
 *          CloudWatch log group configuration
 * @param prefix
 *          Log entry prefix for organizing build logs
 * @param enabled
 *          Whether logging is enabled for this build configuration
 */
public record Logging(
  LogGroupConf logGroup,
  String prefix,
  boolean enabled
) {}
