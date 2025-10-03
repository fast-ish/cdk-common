package fasti.sh.model.main;

public enum Environment {
  BOOTSTRAP, PROTOTYPE, PRODUCTION;

  public static Environment of(Object o) {
    return Environment.valueOf(o.toString().toUpperCase());
  }

  @Override
  public String toString() {
    return name().toLowerCase();
  }
}
