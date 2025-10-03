package fasti.sh.model.aws.apigw.usageplan;

/**
 * Configuration record for API Gateway usage plan throttle settings.
 *
 * <p>
 * Defines rate limiting configuration for API Gateway usage plans including steady-state rate limits and burst capacity.
 *
 * @param rateLimit
 *          Steady-state request rate per second
 * @param burstLimit
 *          Maximum burst capacity for requests
 */
public record ThrottleConf(
  Double rateLimit,
  Integer burstLimit
) {}
