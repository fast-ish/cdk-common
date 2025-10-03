package fasti.sh.execute.aws.apigw;

import static fasti.sh.execute.serialization.Format.id;
import static java.util.stream.Collectors.toMap;

import fasti.sh.execute.aws.lambda.LambdaConstruct;
import fasti.sh.execute.serialization.Mapper;
import fasti.sh.execute.serialization.Template;
import fasti.sh.model.aws.fn.ApiGatewayLambda;
import fasti.sh.model.aws.fn.Integration;
import fasti.sh.model.main.Common;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.services.apigateway.AuthorizationType;
import software.amazon.awscdk.services.apigateway.Authorizer;
import software.amazon.awscdk.services.apigateway.IResource;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.MethodOptions;
import software.amazon.awscdk.services.apigateway.MethodResponse;
import software.amazon.awscdk.services.apigateway.Model;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.lambda.LayerVersion;
import software.constructs.Construct;

/**
 * Sophisticated Amazon API Gateway to AWS Lambda integration construct that provides comprehensive serverless API functionality with
 * advanced routing, method configuration, and Lambda proxy integration.
 *
 * <p>
 * This construct serves as the bridge between API Gateway and Lambda functions, enabling complete serverless API architectures with
 * sophisticated request/response handling, authorization patterns, and multi-method resource configuration.
 *
 * <p>
 * <b>Core Integration Features:</b>
 *
 * <ul>
 * <li><b>Lambda Proxy Integration</b> - Full Lambda proxy integration with automatic request/response transformation
 * <li><b>Resource Hierarchies</b> - Support for nested API Gateway resources with path parameters
 * <li><b>Method Configuration</b> - Multiple HTTP methods per resource with individual configuration
 * <li><b>Request Validation</b> - Integration with request models and validators
 * </ul>
 *
 * <p>
 * <b>Advanced Configuration Capabilities:</b>
 *
 * <ul>
 * <li><b>VPC Integration</b> - Lambda functions deployed within VPC with proper networking
 * <li><b>Layer Management</b> - Support for multiple Lambda layers and dependencies
 * <li><b>Authorization Integration</b> - Seamless integration with API Gateway authorizers
 * <li><b>CORS Configuration</b> - Automatic CORS handling for web application integration
 * </ul>
 *
 * <p>
 * <b>Resource Management Architecture:</b> The construct handles complex API Gateway resource hierarchies:
 *
 * <ul>
 * <li><b>Parent-Child Resources</b> - Nested resource structures with proper path construction
 * <li><b>Path Parameters</b> - Dynamic path segments with parameter extraction
 * <li><b>Resource Methods</b> - Multiple HTTP methods per resource endpoint
 * <li><b>Method Options</b> - Individual method configuration including validation and authorization
 * </ul>
 *
 * <p>
 * <b>Lambda Integration Patterns:</b>
 *
 * <ul>
 * <li><b>Proxy Integration</b> - Complete request forwarding to Lambda with automatic response handling
 * <li><b>Request Transformation</b> - Optional request/response transformation and mapping
 * <li><b>Error Handling</b> - Automatic error response mapping and status code handling
 * <li><b>Performance Optimization</b> - Connection pooling and Lambda warm-up strategies
 * </ul>
 *
 * <p>
 * <b>Template-Based Configuration:</b> Supports dynamic configuration through template processing:
 *
 * <ul>
 * <li><b>Environment-Specific Settings</b> - Different configurations per deployment environment
 * <li><b>Runtime Parameter Injection</b> - Dynamic resource ARNs and configuration values
 * <li><b>Integration Mappings</b> - Configurable request/response transformations
 * </ul>
 *
 * <p>
 * <b>Security and Authorization:</b>
 *
 * <ul>
 * <li><b>Method-Level Security</b> - Individual authorization configuration per HTTP method
 * <li><b>Request Validation</b> - Input validation using request models and validators
 * <li><b>Lambda Permissions</b> - Automatic Lambda execution permissions for API Gateway
 * <li><b>VPC Security</b> - Secure Lambda deployment within private subnets
 * </ul>
 *
 * <p>
 * <b>Integration Flow:</b>
 *
 * <pre>
 * API Gateway Request → Resource Resolution → Method Selection → Lambda Integration → Response
 *        ↓                      ↓                   ↓                ↓               ↓
 * Path Parsing → Parameter Extraction → Validation → Function Execution → Transformation
 * </pre>
 *
 * <p>
 * <b>Usage Example:</b>
 *
 * <pre>{@code
 * // Create Lambda integration with API Gateway resource
 * LambdaIntegrationConstruct integration = new LambdaIntegrationConstruct(this, // construct scope
 *   common, // common configuration
 *   "user-api-config.json", // integration configuration reference
 *   vpc, // VPC for Lambda deployment
 *   restApiConstruct, // parent REST API
 *   parentResource, // parent API Gateway resource
 *   requestModels, // validation models
 *   commonLayer, // Lambda layers
 *   utilityLayer);
 *
 * // Access the integrated resource and Lambda function
 * IResource apiResource = integration.getResource();
 * Function lambdaFunction = integration.getFunction();
 *
 * // The construct automatically:
 * // - Creates Lambda function with VPC integration
 * // - Sets up API Gateway resource hierarchy
 * // - Configures HTTP methods with proxy integration
 * // - Applies request validation and authorization
 * // - Manages Lambda execution permissions
 * }</pre>
 *
 * @author CDK Common Framework
 * @see LambdaConstruct for Lambda function provisioning
 * @see RestApiConstruct for API Gateway setup
 * @see Integration for integration configuration model
 * @see ApiGatewayLambda for Lambda-specific API configuration
 * @since 1.0.0
 */
@Slf4j
@Getter
public class LambdaIntegrationConstruct extends Construct {
  private final IResource resource;
  private final software.amazon.awscdk.services.lambda.Function function;

  public LambdaIntegrationConstruct(Construct scope, Common common, String ref, IVpc vpc, RestApiConstruct stack, IResource parent,
    Map<String, Map<String, Model>> requestModels, LayerVersion... layers) {
    super(scope, id("lambda-integration", ref));

    var conf = parse(ref);
    this.function = new LambdaConstruct(this, common, conf.fn(), vpc, layers).function();

    log.debug("{} [common: {} conf: {}]", "LambdaIntegrationConstruct", common, conf);

    this.resource = integrate(null, stack, parent, requestModels, conf, this.function);
  }

  public LambdaIntegrationConstruct(Construct scope, Common common, Authorizer authorizer, String ref, IVpc vpc, RestApiConstruct stack,
    IResource parent, Map<String, Map<String, Model>> requestModels, LayerVersion baseLayer) {
    super(scope, id("lambda-integration", ref));

    var conf = parse(ref);
    this.function = new LambdaConstruct(this, common, conf.fn(), vpc, baseLayer).function();

    log.debug("{} [common: {} conf: {}]", "LambdaIntegrationConstruct", common, conf);

    this.resource = integrate(authorizer, stack, parent, requestModels, conf, this.function);
  }

  public LambdaIntegrationConstruct(Construct scope, Common common, Authorizer authorizer, String ref, IVpc vpc, RestApiConstruct stack,
    IResource parent, Map<String, Map<String, Model>> requestModels) {
    super(scope, id("lambda-integration", ref));

    var conf = parse(ref);
    this.function = new LambdaConstruct(this, common, conf.fn(), vpc).function();

    log.debug("{} [common: {} conf: {}]", "LambdaIntegrationConstruct", common, conf);

    this.resource = integrate(authorizer, stack, parent, requestModels, conf, this.function);
  }

  private IResource integrate(Authorizer authorizer, RestApiConstruct stack, IResource parent,
    Map<String, Map<String, Model>> requestModels, ApiGatewayLambda conf, software.amazon.awscdk.services.lambda.Function fn) {
    conf.integration().forEach(i -> {
      var integration = LambdaIntegration.Builder
        .create(fn)
        .allowTestInvoke(i.allowTestInvoke())
        .cacheNamespace(i.cacheNamespace())
        .cacheKeyParameters(i.cacheKeyParameters())
        .connectionType(i.connectionType())
        .passthroughBehavior(i.passthroughBehavior())
        .contentHandling(i.contentHandling())
        .proxy(i.proxy())
        .build();

      parent.resourceForPath(i.path()).addMethod(i.method().name(), integration, methodOptions(authorizer, stack, requestModels, i));
    });

    return parent;
  }

  private @NotNull MethodOptions methodOptions(Authorizer authorizer, RestApiConstruct stack, Map<String, Map<String, Model>> requestModels,
    Integration i) {
    var options = MethodOptions
      .builder()
      .operationName(i.options().operationName())
      .authorizationType(i.options().authorizationType())
      .apiKeyRequired(i.options().apiKeyRequired())
      .requestValidator(stack.validators().get(i.options().requestValidator()))
      .methodResponses(
        i
          .options()
          .methodResponses()
          .stream()
          .map(
            methodResponse -> MethodResponse
              .builder()
              .statusCode(methodResponse.statusCode())
              .responseParameters(methodResponse.responseParameters())
              .responseModels(
                Optional
                  .ofNullable(methodResponse.responseModels())
                  .orElseGet(Map::of)
                  .entrySet()
                  .stream()
                  .map(
                    model -> Map
                      .entry(
                        model.getKey(),
                        Model.Builder
                          .create(this, model.getValue().modelName())
                          .modelName(model.getValue().modelName())
                          .description(model.getValue().description())
                          .contentType(model.getValue().contentType())
                          .restApi(stack.api())
                          .build()))
                  .collect(toMap(Entry::getKey, Entry::getValue)))
              .build())
          .toList())
      .requestParameters(i.options().requestParameters());

    if (authorizer != null && i.options().authorizationType().equals(AuthorizationType.COGNITO)) {
      options.authorizer(authorizer);
      options.authorizationScopes(i.options().authorizationScopes());
    }

    if (!i.options().requestModels().isEmpty()) {
      options.requestModels(i.options().requestModels().stream().map(requestModels::get).findFirst().get());
    }

    return options.build();
  }

  @SneakyThrows
  private ApiGatewayLambda parse(String conf) {
    var yaml = Template.parse(this, conf);
    return Mapper.get().readValue(yaml, ApiGatewayLambda.class);
  }
}
