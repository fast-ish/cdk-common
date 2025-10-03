package fasti.sh.model.aws.cloudwatch;

import java.util.Map;
import lombok.Builder;

/**
 * Configuration record for AWS CloudWatch dashboard settings.
 *
 * <p>
 * Defines the configuration for CloudWatch dashboards including dashboard content and metadata.
 *
 * @param name
 *          Dashboard name identifier
 * @param body
 *          Dashboard body content in JSON format
 * @param tags
 *          Resource tags for the dashboard
 */
@Builder
public record DashboardConf(
  String name,
  String body,
  Map<String, String> tags
) {}
