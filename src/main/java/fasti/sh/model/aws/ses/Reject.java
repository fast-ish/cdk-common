package fasti.sh.model.aws.ses;

public record Reject(
  boolean enabled,
  String topic,
  String configurationSet
) {}
