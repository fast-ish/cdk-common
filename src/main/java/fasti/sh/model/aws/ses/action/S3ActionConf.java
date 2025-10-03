package fasti.sh.model.aws.ses.action;

public record S3ActionConf(
  String prefix,
  String topic
) {}
