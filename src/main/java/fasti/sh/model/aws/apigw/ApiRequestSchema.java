package fasti.sh.model.aws.apigw;

import software.amazon.awscdk.services.apigateway.JsonSchema;

/**
 * Functional interface for API Gateway request schema resolution.
 *
 * <p>
 * Provides a contract for resolving JSON schemas based on schema identifiers, enabling dynamic schema lookup for API Gateway request
 * validation.
 *
 * @param schema
 *          The schema identifier string
 * @return JsonSchema instance for API Gateway validation
 */
@FunctionalInterface
public interface ApiRequestSchema {
  JsonSchema get(String schema);
}
