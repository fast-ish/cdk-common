package fasti.sh.model.aws.ses.action;

import fasti.sh.model.aws.fn.Lambda;

public record LambdaActionConf(
  String topic,
  Lambda function,
  String invocationType
) {}
