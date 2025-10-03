package fasti.sh.model.aws.ses;

public record DedicatedIpPool(
  boolean enabled,
  String name,
  String scalingMode
) {}
