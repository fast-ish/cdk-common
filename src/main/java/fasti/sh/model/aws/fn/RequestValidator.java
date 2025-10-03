package fasti.sh.model.aws.fn;

public record RequestValidator(
  String name,
  boolean validateRequestParameters,
  boolean validateRequestBody
) {}
