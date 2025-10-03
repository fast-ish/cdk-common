package fasti.sh.model.aws.apigw;

/**
 * Configuration record for AWS API Gateway response model settings.
 *
 * <p>
 * Defines response model configuration for API Gateway including model identification, description, and content type specification.
 *
 * @param modelName
 *          Name identifier for the response model
 * @param description
 *          Human-readable description of the model
 * @param contentType
 *          MIME content type for the response model
 */
public record ResponseModel(
  String modelName,
  String description,
  String contentType
) {}
