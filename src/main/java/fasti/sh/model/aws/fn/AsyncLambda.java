package fasti.sh.model.aws.fn;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import fasti.sh.model.aws.iam.IamRole;
import java.util.Map;
import lombok.Builder;
import lombok.Value;
import software.amazon.awscdk.services.lambda.Runtime;

/**
 * Configuration model for asynchronous Lambda functions with SQS integration. Supports decoupled processing patterns for time-sensitive
 * triggers like Cognito.
 *
 * @author CDK Common Framework
 * @since 1.0.0
 */
@Value
@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = AsyncLambda.Builder.class)
public class AsyncLambda {
  String name;
  String description;
  String asset;
  String handler;
  Runtime runtime;
  Integer timeout;
  Integer memorySize;
  String subnetType;
  Map<String, String> environment;
  IamRole role;

  // Queue configuration
  Integer retentionDays;
  Integer maxRetries;

  // Processor configuration
  Integer batchSize;
  Integer maxBatchingWindowSeconds;
  Integer reservedConcurrentExecutions;

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    // Default values
    private Integer timeout = 300;
    private Integer memorySize = 512;
    private String subnetType = "PRIVATE_WITH_EGRESS";
    private Integer retentionDays = 7;
    private Integer maxRetries = 3;
    private Integer batchSize = 10;
    private Integer maxBatchingWindowSeconds = 5;
    private Integer reservedConcurrentExecutions = 10;
    private Map<String, String> environment = Map.of();
  }
}
