package fasti.sh.model.aws.cognito.userpool;

public record AutoVerify(
  boolean email,
  boolean phone
) {}
