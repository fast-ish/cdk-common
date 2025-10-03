package fasti.sh.model.aws.apigw;

/**
 * Configuration record for API Gateway VPC Link settings.
 *
 * <p>
 * Defines VPC Link configuration for connecting API Gateway to resources within a Virtual Private Cloud (VPC).
 *
 * @param name
 *          VPC Link name identifier
 */
public record VpcLink(String name) {}
