package fasti.sh.model.aws.apigw;

import java.util.List;

/**
 * Configuration record for API Gateway CORS (Cross-Origin Resource Sharing) options.
 *
 * <p>
 * Defines CORS configuration for API Gateway including allowed origins, headers, and HTTP methods for cross-origin requests.
 *
 * @param allowOrigins
 *          List of allowed origin domains for CORS requests
 * @param allowHeaders
 *          List of allowed HTTP headers in CORS requests
 * @param allowMethods
 *          List of allowed HTTP methods for CORS requests
 */
public record CorsOptions(
  List<String> allowOrigins,
  List<String> allowHeaders,
  List<String> allowMethods
) {}
