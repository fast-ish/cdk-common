package fasti.sh.model.aws.codebuild;

/**
 * Configuration record for hosted pipeline settings.
 *
 * <p>
 * Combines pipeline host configuration with hosted service configuration to create a complete hosted pipeline setup.
 *
 * @param <T>
 *          The type parameter for the pipeline host configuration
 * @param <U>
 *          The type parameter for the hosted service configuration
 * @param host
 *          Pipeline host configuration
 * @param hosted
 *          Hosted service configuration
 */
public record PipelineHosted<T, U>(
  PipelineHost<T> host,
  U hosted
) {}
