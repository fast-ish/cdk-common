package fasti.sh.model.aws.cognito.userpool;

public record UserAttribute(
  boolean required,
  boolean mutable
) {}
