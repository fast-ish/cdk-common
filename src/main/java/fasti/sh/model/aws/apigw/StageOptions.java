package fasti.sh.model.aws.apigw;

import java.util.Map;

/**
 * Configuration record for AWS API Gateway stage options.
 *
 * <p>
 * Defines comprehensive stage configuration including deployment settings, logging, caching, metrics, tracing, and throttling parameters.
 *
 * @param stageName
 *          Name of the API Gateway stage
 * @param description
 *          Human-readable description of the stage
 * @param loggingLevel
 *          CloudWatch logging level for the stage
 * @param variables
 *          Stage variables for configuration
 * @param cachingEnabled
 *          Whether caching is enabled for the stage
 * @param dataTraceEnabled
 *          Whether data trace logging is enabled
 * @param metricsEnabled
 *          Whether CloudWatch metrics collection is enabled
 * @param tracingEnabled
 *          Whether AWS X-Ray tracing is enabled
 * @param throttlingBurstLimit
 *          Maximum burst capacity for throttling
 * @param throttlingRateLimit
 *          Steady-state rate limit for throttling
 */
public record StageOptions(
  String stageName,
  String description,
  String loggingLevel,
  Map<String, String> variables,
  boolean cachingEnabled,
  boolean dataTraceEnabled,
  boolean metricsEnabled,
  boolean tracingEnabled,
  int throttlingBurstLimit,
  int throttlingRateLimit
) {}
