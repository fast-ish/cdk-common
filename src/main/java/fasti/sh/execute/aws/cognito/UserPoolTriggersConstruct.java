package fasti.sh.execute.aws.cognito;

import static fasti.sh.execute.serialization.Format.id;

import fasti.sh.execute.aws.lambda.AsyncLambdaConstruct;
import fasti.sh.execute.aws.lambda.LambdaConstruct;
import fasti.sh.execute.serialization.Mapper;
import fasti.sh.execute.serialization.Template;
import fasti.sh.model.aws.cognito.userpool.Triggers;
import fasti.sh.model.aws.cognito.userpool.UserPoolConf;
import fasti.sh.model.aws.fn.Lambda;
import fasti.sh.model.main.Common;
import java.util.List;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.services.cognito.UserPoolTriggers;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.IFunction;
import software.amazon.awscdk.services.lambda.LayerVersion;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;

@Slf4j
@Getter
public class UserPoolTriggersConstruct {
  private final UserPoolTriggers triggers;
  private final Queue queue;
  private final AsyncLambdaConstruct asyncProcessor;

  @SneakyThrows
  public UserPoolTriggersConstruct(Construct scope, Common common, IVpc vpc, UserPoolConf conf) {
    log.debug("{} [common: {} conf: {}]", "UserPoolTriggersConstruct", common, conf);

    var triggersYaml = Template.parse(scope, conf.triggers());
    var triggersConf = Mapper.get().readValue(triggersYaml, Triggers.class);

    var baseLayer = LayerVersion.Builder
      .create(scope, id("layer", triggersConf.base().name()))
      .layerVersionName(triggersConf.base().name())
      .code(Code.fromAsset(triggersConf.base().asset()))
      .removalPolicy(triggersConf.base().removalPolicy())
      .compatibleArchitectures(List.of(Architecture.X86_64))
      .compatibleRuntimes(triggersConf.base().runtimes().stream().map(r -> Runtime.Builder.create(r).build()).toList())
      .build();

    if (triggersConf.postEventProcessor() != null) {
      this.asyncProcessor = new AsyncLambdaConstruct(scope, common, triggersConf.postEventProcessor(), vpc, baseLayer);
      this.queue = asyncProcessor.queue();

      if (triggersConf.postAuthentication() != null) {
        triggersConf.postAuthentication().environment().put("QUEUE_URL", queue.getQueueUrl());
      }
      if (triggersConf.postConfirmation() != null) {
        triggersConf.postConfirmation().environment().put("QUEUE_URL", queue.getQueueUrl());
      }
    } else {
      this.asyncProcessor = null;
      this.queue = null;
    }

    this.triggers = UserPoolTriggers
      .builder()
      .customMessage(maybe(scope, common, vpc, triggersConf.customMessage(), baseLayer))
      .preSignUp(maybe(scope, common, vpc, triggersConf.preSignUp(), baseLayer))
      .postConfirmation(maybe(scope, common, vpc, triggersConf.postConfirmation(), baseLayer))
      .postAuthentication(maybe(scope, common, vpc, triggersConf.postAuthentication(), baseLayer))
      .build();
  }

  private static @Nullable IFunction maybe(Construct scope, Common common, IVpc vpc, Lambda lambda, LayerVersion baseLayer) {
    return lambda != null ? new LambdaConstruct(scope, common, lambda, vpc, baseLayer).function() : null;
  }
}
