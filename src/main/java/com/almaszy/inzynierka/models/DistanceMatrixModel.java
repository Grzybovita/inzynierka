package com.almaszy.inzynierka.models;

public class DistanceMatrixModel {

  private long[][] distanceMatrix;
  private int vehicleNumber = 1;
  private int depot = 0;

  public DistanceMatrixModel(long[][] distanceMatrix) {
    this.distanceMatrix = distanceMatrix;
  }

  public long[][] getDistanceMatrix() {
    return distanceMatrix;
  }

  public void setDistanceMatrix(long[][] distanceMatrix) {
    this.distanceMatrix = distanceMatrix;
  }

  public int getVehicleNumber() {
    return vehicleNumber;
  }

  public void setVehicleNumber(int vehicleNumber) {
    this.vehicleNumber = vehicleNumber;
  }

  public int getDepot() {
    return depot;
  }

  public void setDepot(int depot) {
    this.depot = depot;
  }
}
