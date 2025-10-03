package fasti.sh.model.aws.bcm;

public record DestinationConfigurations(
  String region,
  String bucket,
  String prefix,
  OutputConfiguration outputConfigurations
) {}
