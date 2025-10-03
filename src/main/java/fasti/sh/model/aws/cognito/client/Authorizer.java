package fasti.sh.model.aws.cognito.client;

/**
 * Configuration record for Cognito authorizer settings.
 *
 * <p>
 * Defines authorizer configuration for Cognito integration with API Gateway or other services.
 *
 * @param name
 *          Authorizer name identifier
 */
public record Authorizer(String name) {}
