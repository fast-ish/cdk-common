package fasti.sh.model.aws.cognito.userpool;

public record Mfa(
  String type,
  String message,
  boolean sms,
  boolean otp
) {}
