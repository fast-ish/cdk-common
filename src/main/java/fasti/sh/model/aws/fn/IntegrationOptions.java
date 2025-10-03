package fasti.sh.model.aws.fn;

import fasti.sh.model.aws.apigw.MethodResponse;
import java.util.List;
import java.util.Map;
import software.amazon.awscdk.services.apigateway.AuthorizationType;

public record IntegrationOptions(
  String operationName,
  AuthorizationType authorizationType,
  boolean apiKeyRequired,
  List<String> authorizationScopes,
  List<String> requestModels,
  String requestValidator,
  Map<String, Boolean> requestParameters,
  List<MethodResponse> methodResponses
) {}
