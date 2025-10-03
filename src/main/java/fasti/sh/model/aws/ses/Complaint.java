package fasti.sh.model.aws.ses;

public record Complaint(
  boolean enabled,
  String topic,
  String configurationSet
) {}
