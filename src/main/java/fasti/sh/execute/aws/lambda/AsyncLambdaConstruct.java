package fasti.sh.execute.aws.lambda;

import static fasti.sh.execute.serialization.Format.id;

import fasti.sh.execute.aws.iam.RoleConstruct;
import fasti.sh.model.aws.fn.AsyncLambda;
import fasti.sh.model.main.Common;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.ec2.SubnetSelection;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.LayerVersion;
import software.amazon.awscdk.services.lambda.eventsources.SqsEventSource;
import software.amazon.awscdk.services.sqs.DeadLetterQueue;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;

/**
 * Async Lambda construct that combines a Lambda function with SQS queue for asynchronous processing. This construct is designed for
 * scenarios where immediate response is required (like Cognito triggers) but the actual processing can be deferred.
 *
 * <p>
 * <b>Architecture:</b>
 *
 * <pre>
 * Cognito Trigger → Primary Lambda → SQS Queue → Async Processor Lambda
 *        ↓              ↓                ↓              ↓
 *   Quick Response  Enqueue Message  Buffer     Background Processing
 * </pre>
 *
 * <p>
 * <b>Key Features:</b>
 * <ul>
 * <li>Decouples synchronous triggers from async processing</li>
 * <li>Provides resilient message handling with DLQ support</li>
 * <li>Configurable batch processing and concurrency</li>
 * <li>Automatic retry and error handling</li>
 * </ul>
 *
 * @author CDK Common Framework
 * @since 1.0.0
 */
@Slf4j
@Getter
public class AsyncLambdaConstruct extends Construct {
  private final Function processorFunction;
  private final Queue queue;
  private final Queue deadLetterQueue;

  public AsyncLambdaConstruct(Construct scope, Common common, AsyncLambda conf, IVpc vpc) {
    super(scope, id("async.lambda", conf.name()));

    log.debug("{} [common: {} conf: {}]", "AsyncLambdaConstruct", common, conf);

    this.deadLetterQueue = Queue.Builder
      .create(this, id("dlq", conf.name()))
      .queueName(conf.name())
      .retentionPeriod(Duration.days(14))
      .build();

    this.queue = Queue.Builder
      .create(this, id("queue", conf.name()))
      .queueName(conf.name())
      .visibilityTimeout(Duration.seconds(conf.timeout() * 6))
      .retentionPeriod(Duration.days(conf.retentionDays()))
      .deadLetterQueue(DeadLetterQueue.builder().queue(deadLetterQueue).maxReceiveCount(conf.maxRetries()).build())
      .build();

    var role = new RoleConstruct(this, common, conf.role()).role();

    role
      .addToPolicy(
        PolicyStatement.Builder
          .create()
          .effect(Effect.ALLOW)
          .actions(
            List
              .of(
                "sqs:ReceiveMessage",
                "sqs:DeleteMessage",
                "sqs:GetQueueAttributes",
                "sqs:ChangeMessageVisibility"))
          .resources(List.of(queue.getQueueArn()))
          .build());

    this.processorFunction = Function.Builder
      .create(this, conf.name())
      .vpc(vpc)
      .vpcSubnets(SubnetSelection.builder().subnetType(SubnetType.valueOf(conf.subnetType().toUpperCase())).build())
      .role(role)
      .functionName(conf.name())
      .description(conf.description())
      .runtime(conf.runtime())
      .architecture(Architecture.X86_64)
      .code(Code.fromAsset(conf.asset()))
      .environment(addQueueEnvironment(conf.environment()))
      .handler(conf.handler())
      .timeout(Duration.seconds(conf.timeout()))
      .memorySize(conf.memorySize())
      .reservedConcurrentExecutions(conf.reservedConcurrentExecutions())
      .build();

    processorFunction
      .addEventSource(
        SqsEventSource.Builder
          .create(queue)
          .batchSize(conf.batchSize())
          .maxBatchingWindow(Duration.seconds(conf.maxBatchingWindowSeconds()))
          .reportBatchItemFailures(true)
          .build());

    log
      .info(
        "created async lambda construct with queue {} and processor {}",
        queue.getQueueName(),
        processorFunction.getFunctionName());
  }

  public AsyncLambdaConstruct(Construct scope, Common common, AsyncLambda conf, IVpc vpc, LayerVersion... layers) {
    super(scope, id("async.lambda", conf.name()));

    log.debug("{} [common: {} conf: {} layers: {}]", "AsyncLambdaConstruct", common, conf, layers.length);

    this.deadLetterQueue =
      Queue.Builder
        .create(this, id("dlq", conf.name()))
        .queueName(conf.name() + "-dlq")
        .retentionPeriod(Duration.days(14))
        .build();

    this.queue = Queue.Builder
      .create(this, id("queue", conf.name()))
      .queueName(conf.name())
      .visibilityTimeout(Duration.seconds(conf.timeout() * 6))
      .retentionPeriod(Duration.days(conf.retentionDays()))
      .deadLetterQueue(DeadLetterQueue.builder().queue(deadLetterQueue).maxReceiveCount(conf.maxRetries()).build())
      .build();

    var role = new RoleConstruct(this, common, conf.role()).role();

    role
      .addToPolicy(
        PolicyStatement.Builder
          .create()
          .effect(Effect.ALLOW)
          .actions(
            List
              .of(
                "sqs:ReceiveMessage",
                "sqs:DeleteMessage",
                "sqs:GetQueueAttributes",
                "sqs:ChangeMessageVisibility"))
          .resources(List.of(queue.getQueueArn()))
          .build());

    this.processorFunction = Function.Builder
      .create(this, conf.name())
      .vpc(vpc)
      .vpcSubnets(SubnetSelection.builder().subnetType(SubnetType.valueOf(conf.subnetType().toUpperCase())).build())
      .role(role)
      .functionName(conf.name())
      .description(conf.description())
      .runtime(conf.runtime())
      .architecture(Architecture.X86_64)
      .code(Code.fromAsset(conf.asset()))
      .environment(addQueueEnvironment(conf.environment()))
      .handler(conf.handler())
      .timeout(Duration.seconds(conf.timeout()))
      .memorySize(conf.memorySize())
      .reservedConcurrentExecutions(conf.reservedConcurrentExecutions())
      .layers(List.of(layers))
      .build();

    processorFunction
      .addEventSource(
        SqsEventSource.Builder
          .create(queue)
          .batchSize(conf.batchSize())
          .maxBatchingWindow(Duration.seconds(conf.maxBatchingWindowSeconds()))
          .reportBatchItemFailures(true)
          .build());

    log
      .info(
        "created async lambda construct with queue {} and processor {}",
        queue.getQueueName(),
        processorFunction.getFunctionName());
  }

  private Map<String, String> addQueueEnvironment(Map<String, String> environment) {
    var enriched = new java.util.HashMap<>(environment);
    enriched.put("QUEUE_URL", queue.getQueueUrl());
    enriched.put("DLQ_URL", deadLetterQueue.getQueueUrl());
    return enriched;
  }
}
