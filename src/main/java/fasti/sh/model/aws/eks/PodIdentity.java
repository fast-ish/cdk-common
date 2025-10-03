package fasti.sh.model.aws.eks;

import fasti.sh.model.aws.iam.IamRole;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import java.util.Map;

public record PodIdentity(
  ObjectMeta metadata,
  IamRole role,
  Map<String, String> tags
) {}
