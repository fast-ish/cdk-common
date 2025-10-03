package fasti.sh.model.aws.cloudwatch;

import java.util.List;
import java.util.Map;
import lombok.Builder;

/**
 * Configuration record for AWS CloudWatch observability settings.
 *
 * <p>
 * Defines comprehensive observability configuration including SNS topics, metric filters, alarms, and dashboards for monitoring and
 * alerting.
 *
 * @param topics
 *          Map of topic names to subscriber lists for notifications
 * @param metrics
 *          List of metric filter configurations
 * @param alarms
 *          List of alarm configurations
 * @param dashboards
 *          List of dashboard configurations
 */
@Builder
public record ObservabilityConf(
  Map<String, List<String>> topics,
  List<MetricFilterConf> metrics,
  List<AlarmConf> alarms,
  List<DashboardConf> dashboards
) {}
