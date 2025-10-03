package fasti.sh.model.aws.fn;

import fasti.sh.model.aws.iam.IamRole;
import fasti.sh.model.aws.iam.Principal;
import java.util.List;
import java.util.Map;
import software.amazon.awscdk.services.lambda.Runtime;

/**
 * Configuration record for AWS Lambda function infrastructure.
 *
 * <p>
 * Defines comprehensive Lambda function configuration including runtime settings, IAM roles, networking, layers, and environment variables.
 *
 * @param name
 *          Unique function name identifier
 * @param description
 *          Human-readable description of the function purpose
 * @param asset
 *          Path to the deployment package (ZIP file or container image)
 * @param handler
 *          Entry point for the Lambda function execution
 * @param subnetType
 *          VPC subnet type for network configuration
 * @param timeout
 *          Maximum execution time in seconds
 * @param memorySize
 *          Memory allocation in MB (128-10240)
 * @param runtime
 *          Lambda runtime environment (e.g., JAVA_11, PYTHON_3_9)
 * @param role
 *          IAM role configuration for function execution
 * @param invokers
 *          List of principals allowed to invoke this function
 * @param layers
 *          List of Lambda layers for shared code and dependencies
 * @param environment
 *          Map of environment variables for function configuration
 */
public record Lambda(
  String name,
  String description,
  String asset,
  String handler,
  String subnetType,
  int timeout,
  int memorySize,
  Runtime runtime,
  IamRole role,
  List<Principal> invokers,
  List<LambdaLayer> layers,
  Map<String, String> environment
) {}
