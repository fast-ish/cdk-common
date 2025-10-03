package fasti.sh.model.aws.apigw.usageplan;

/**
 * Configuration record for API Gateway usage plan settings.
 *
 * <p>
 * Defines comprehensive usage plan configuration including throttling limits and quota settings for API access control.
 *
 * @param name
 *          Usage plan name identifier
 * @param description
 *          Human-readable description of the usage plan
 * @param throttle
 *          Throttling configuration for rate limiting
 * @param quota
 *          Quota configuration for request limits
 */
public record UsagePlanConf(
  String name,
  String description,
  ThrottleConf throttle,
  QuotaConf quota
) {}
