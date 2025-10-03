package fasti.sh.model.aws.eks;

import fasti.sh.model.aws.iam.IamRole;
import io.fabric8.kubernetes.api.model.ObjectMeta;

public record ServiceAccountConf(
  ObjectMeta metadata,
  IamRole role
) {}
