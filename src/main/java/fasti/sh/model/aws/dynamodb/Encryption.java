package fasti.sh.model.aws.dynamodb;

import fasti.sh.model.aws.kms.Kms;

public record Encryption(
  boolean enabled,
  String owner,
  Kms kms
) {}
