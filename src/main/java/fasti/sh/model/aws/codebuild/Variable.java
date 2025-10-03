package fasti.sh.model.aws.codebuild;

/**
 * Configuration record for pipeline and build environment variables.
 *
 * <p>
 * Defines variable configuration with name and default value for use in CodePipeline and CodeBuild environments.
 *
 * @param name
 *          Variable name identifier
 * @param defaults
 *          Default value for the variable
 */
public record Variable(
  String name,
  String defaults
) {}
