package fasti.sh.model.aws.cloudwatch;

import lombok.Builder;

/**
 * Configuration record for AWS CloudWatch metric filter settings.
 *
 * <p>
 * Defines the configuration for creating CloudWatch metric filters that extract metrics from log data based on filter patterns.
 *
 * @param filterName
 *          Name of the metric filter
 * @param logGroupName
 *          CloudWatch log group name to filter
 * @param filterPattern
 *          Pattern to match in log entries
 * @param metricNamespace
 *          CloudWatch metric namespace
 * @param metricName
 *          Name of the extracted metric
 * @param metricValue
 *          Value expression for the metric
 * @param defaultValue
 *          Default value when no matches are found
 */
@Builder
public record MetricFilterConf(
  String filterName,
  String logGroupName,
  String filterPattern,
  String metricNamespace,
  String metricName,
  String metricValue,
  Double defaultValue
) {}
