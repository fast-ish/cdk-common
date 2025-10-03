package fasti.sh.model.aws.eks.addon.core.karpenter;

import fasti.sh.model.aws.eks.HelmChart;
import fasti.sh.model.aws.eks.PodIdentity;

public record KarpenterAddon(
  HelmChart chart,
  PodIdentity podIdentity
) {}
