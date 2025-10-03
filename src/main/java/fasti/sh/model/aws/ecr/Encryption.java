package fasti.sh.model.aws.ecr;

import fasti.sh.model.aws.kms.Kms;

public record Encryption(
  boolean enabled,
  Kms kms
) {}
