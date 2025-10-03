package fasti.sh.model.aws.ses;

public record Bounce(
  boolean enabled,
  String topic,
  String configurationSet
) {}
