package de.envite.greenbpm.carbonreductorconnector.service;

public enum Locations {
  EUROPE_NORTH("northeurope"),
  EUROPE_WEST("westeurope"),
  FRANCE_CENTRAL("francecentral"),
  FRANCE_SOUTH("francesouth"),
  GERMANY_NORTH("germanynorth"),
  GERMANY_WEST_CENTRAL("germanywestcentral"),
  UK_SOUTH("uksouth"),
  UK_WEST("ukwest"),
  SWITZERLAND_NORTH("switzerlandnorth"),
  SWITZERLAND_WEST("switzerlandwest"),
  SWEDEN_CENTRAL("swedencentral"),
  NORWAY_EAST("norwayeast");

  Locations(String regionname) {
    this.regionname = regionname;
  }

  private String regionname;

  public String regionname() {
    return this.regionname;
  }
}
