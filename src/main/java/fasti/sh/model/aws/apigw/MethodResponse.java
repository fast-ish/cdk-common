package fasti.sh.model.aws.apigw;

import java.util.Map;

/**
 * Configuration record for API Gateway method response settings.
 *
 * <p>
 * Defines response configuration for API Gateway methods including status codes, response models, and response parameters.
 *
 * @param statusCode
 *          HTTP status code for the response
 * @param responseModels
 *          Map of content types to response model configurations
 * @param responseParameters
 *          Map of response parameter names to required flags
 */
public record MethodResponse(
  String statusCode,
  Map<String, ResponseModel> responseModels,
  Map<String, Boolean> responseParameters
) {}
