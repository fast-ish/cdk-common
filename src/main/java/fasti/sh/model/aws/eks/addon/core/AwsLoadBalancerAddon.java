package fasti.sh.model.aws.eks.addon.core;

import fasti.sh.model.aws.eks.HelmChart;
import fasti.sh.model.aws.eks.ServiceAccountConf;

public record AwsLoadBalancerAddon(
  HelmChart chart,
  ServiceAccountConf serviceAccount
) {}
