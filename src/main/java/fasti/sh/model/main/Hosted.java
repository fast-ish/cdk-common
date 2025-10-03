package fasti.sh.model.main;

public record Hosted<T, U>(
  Host<T> host,
  U hosted
) {}
