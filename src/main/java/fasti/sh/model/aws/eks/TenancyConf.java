package fasti.sh.model.aws.eks;

import java.util.List;

public record TenancyConf(
  List<Tenant> administrators,
  List<Tenant> users
) {}
