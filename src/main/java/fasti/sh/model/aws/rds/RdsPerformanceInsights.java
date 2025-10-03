package fasti.sh.model.aws.rds;

public record RdsPerformanceInsights(
  Boolean enabled,
  String retention
) {}
