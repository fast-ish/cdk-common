package fasti.sh.model.aws.bcm;

public record OutputConfiguration(
  String compression,
  String format,
  String outputType,
  String overwrite
) {}
