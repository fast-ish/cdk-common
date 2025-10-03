package fasti.sh.model.aws.cognito.userpool;

public record KeepOriginalAttributes(
  boolean email,
  boolean phone
) {}
