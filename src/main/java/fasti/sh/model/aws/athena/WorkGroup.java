package fasti.sh.model.aws.athena;

public record WorkGroup(
  String name,
  String output,
  boolean enforceWorkGroupConfiguration,
  boolean publishCloudWatchMetricsEnabled,
  long bytesScannedCutoffPerQuery
) {}
