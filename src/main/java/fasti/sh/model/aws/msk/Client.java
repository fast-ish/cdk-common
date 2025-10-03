package fasti.sh.model.aws.msk;

import fasti.sh.model.aws.eks.ServiceAccountConf;

public record Client(
  String name,
  ServiceAccountConf serviceAccount
) {}
