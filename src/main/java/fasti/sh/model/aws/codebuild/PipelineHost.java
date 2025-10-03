package fasti.sh.model.aws.codebuild;

import fasti.sh.model.main.Common;

/**
 * Configuration record for pipeline host settings.
 *
 * <p>
 * Defines the infrastructure configuration for hosting a pipeline including common settings, source configuration, pipeline definition, and
 * synthesizer.
 *
 * @param <T>
 *          The type parameter for the pipeline host configuration
 * @param common
 *          Common configuration settings
 * @param source
 *          CodeStar connection source configuration
 * @param pipeline
 *          Pipeline configuration details
 * @param synthesizer
 *          CDK synthesizer configuration
 */
public record PipelineHost<T>(
  Common common,
  CodeStarConnectionSource source,
  Pipeline pipeline,
  String synthesizer
) {}
