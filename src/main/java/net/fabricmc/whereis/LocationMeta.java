package net.fabricmc.whereis;

public class LocationMeta {
  public String owner;
  public String alias;
  public String dimension;

  public LocationMeta(
    String owner,
    String alias,
    String dimension
  ) {
    this.owner = owner;
    this.alias = alias;
    this.dimension = dimension;
  }
}
