package fasti.sh.model.aws.apigw.usageplan;

import software.amazon.awscdk.services.apigateway.Period;

/**
 * Configuration record for API Gateway usage plan quota settings.
 *
 * <p>
 * Defines request quota configuration for API Gateway usage plans including quota limits, time periods, and enablement settings.
 *
 * @param enabled
 *          Whether the quota is enabled for the usage plan
 * @param limit
 *          Maximum number of requests allowed within the period
 * @param period
 *          Time period for quota calculation (daily, weekly, monthly)
 */
public record QuotaConf(
  boolean enabled,
  Integer limit,
  Period period
) {}
