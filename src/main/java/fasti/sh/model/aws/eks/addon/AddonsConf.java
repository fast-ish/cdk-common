package fasti.sh.model.aws.eks.addon;

import fasti.sh.model.aws.eks.addon.core.AlloyOperatorAddon;
import fasti.sh.model.aws.eks.addon.core.AwsLoadBalancerAddon;
import fasti.sh.model.aws.eks.addon.core.AwsSecretsStoreAddon;
import fasti.sh.model.aws.eks.addon.core.CertManagerAddon;
import fasti.sh.model.aws.eks.addon.core.GrafanaAddon;
import fasti.sh.model.aws.eks.addon.core.karpenter.KarpenterAddon;
import fasti.sh.model.aws.eks.addon.managed.ManagedAddons;

public record AddonsConf(
  ManagedAddons managed,
  AwsSecretsStoreAddon awsSecretsStore,
  AwsLoadBalancerAddon awsLoadBalancer,
  CertManagerAddon certManager,
  KarpenterAddon karpenter,
  AlloyOperatorAddon alloyOperator,
  GrafanaAddon grafana
) {}
