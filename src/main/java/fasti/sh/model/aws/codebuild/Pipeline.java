package fasti.sh.model.aws.codebuild;

import java.util.List;
import software.amazon.awscdk.services.codepipeline.ExecutionMode;
import software.amazon.awscdk.services.codepipeline.PipelineType;

/**
 * Configuration record for AWS CodePipeline infrastructure automation.
 *
 * <p>
 * Defines a complete pipeline configuration for continuous integration and deployment, including source repositories, execution settings,
 * and deployment targets.
 *
 * @param name
 *          Unique identifier for the pipeline
 * @param description
 *          Human-readable description of the pipeline purpose
 * @param pipelineType
 *          Type of pipeline (V1 or V2)
 * @param executionMode
 *          Pipeline execution mode (QUEUED, SUPERSEDED, or PARALLEL)
 * @param variables
 *          List of pipeline-level variables
 * @param crossAccountKeys
 *          Whether to enable cross-account KMS keys
 * @param restartExecutionOnUpdate
 *          Whether to restart execution when pipeline is updated
 * @param cdkRepo
 *          Source repository configuration for CDK code
 * @param deployment
 *          Source repository configuration for deployment artifacts
 */
public record Pipeline(
  String name,
  String description,
  PipelineType pipelineType,
  ExecutionMode executionMode,
  List<Variable> variables,
  boolean crossAccountKeys,
  boolean restartExecutionOnUpdate,
  CodeStarConnectionSource cdkRepo,
  CodeStarConnectionSource deployment
) {}
