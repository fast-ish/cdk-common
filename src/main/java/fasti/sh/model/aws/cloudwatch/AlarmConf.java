package fasti.sh.model.aws.cloudwatch;

import java.util.List;
import java.util.Map;
import lombok.Builder;

/**
 * Configuration record for AWS CloudWatch alarm settings.
 *
 * <p>
 * Defines the configuration for CloudWatch alarms including metric details, thresholds, evaluation criteria, and actions to take when alarm
 * state changes.
 *
 * @param name
 *          Alarm name identifier
 * @param description
 *          Human-readable description of the alarm
 * @param metricNamespace
 *          CloudWatch metric namespace
 * @param metricName
 *          Name of the metric to monitor
 * @param statistic
 *          Statistical function to apply to metric data
 * @param dimensions
 *          Metric dimensions for filtering
 * @param periodMinutes
 *          Evaluation period in minutes
 * @param evaluationPeriods
 *          Number of periods to evaluate
 * @param threshold
 *          Threshold value for alarm evaluation
 * @param comparisonOperator
 *          Comparison operator for threshold evaluation
 * @param treatMissingData
 *          How to treat missing data points
 * @param alarmActions
 *          List of actions to execute when alarm triggers
 * @param tags
 *          Resource tags for the alarm
 */
@Builder
public record AlarmConf(
  String name,
  String description,
  String metricNamespace,
  String metricName,
  String statistic,
  Map<String, String> dimensions,
  Integer periodMinutes,
  Integer evaluationPeriods,
  Double threshold,
  String comparisonOperator,
  String treatMissingData,
  List<String> alarmActions,
  Map<String, String> tags
) {}
