package fasti.sh.model.aws.vpc.securitygroup;

public record SecurityGroupIpRule(
  String ip,
  int startPort,
  int endPort
) {}
