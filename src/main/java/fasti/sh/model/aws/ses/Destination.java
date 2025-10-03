package fasti.sh.model.aws.ses;

public record Destination(
  Bounce bounce,
  Reject reject,
  Complaint complaint
) {}
