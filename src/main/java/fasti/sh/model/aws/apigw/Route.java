package fasti.sh.model.aws.apigw;

import java.util.List;

/**
 * Configuration record for API Gateway route settings.
 *
 * <p>
 * Defines HTTP route configuration including supported methods and the URL path pattern for API Gateway routing.
 *
 * @param methods
 *          List of HTTP methods supported by this route
 * @param path
 *          URL path pattern for the route
 */
public record Route(
  List<String> methods,
  String path
) {}
