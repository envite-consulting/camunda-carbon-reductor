package de.envite.greenbpm.carbonreductorconnector.domain.model;

import lombok.Builder;

import java.util.Objects;

@Builder
public class CarbonReductorOutput {

  private boolean executionDelayed;
  private double originalCarbon;
  private double actualCarbon;
  private double savedCarbon;
  private long delayedBy;

  public boolean isExecutionDelayed() {
    return executionDelayed;
  }

  public void setExecutionDelayed(boolean executionDelayed) {
    this.executionDelayed = executionDelayed;
  }

  public double getOriginalCarbon() {
    return originalCarbon;
  }

  public void setOriginalCarbon(double originalCarbon) {
    this.originalCarbon = originalCarbon;
  }

  public double getActualCarbon() {
    return actualCarbon;
  }

  public void setActualCarbon(double actualCarbon) {
    this.actualCarbon = actualCarbon;
  }

  public double getSavedCarbon() {
    return savedCarbon;
  }

  public void setSavedCarbon(double savedCarbon) {
    this.savedCarbon = savedCarbon;
  }

  public long getDelayedBy() {
    return delayedBy;
  }

  public void setDelayedBy(long delayedBy) {
    this.delayedBy = delayedBy;
  }

  @Override
  public String toString() {
    return "GreenTimerOutput{"
        + "executionDelayed="
        + executionDelayed
        + ", originalCarbon="
        + originalCarbon
        + ", actualCarbon="
        + actualCarbon
        + ", savedCarbon="
        + savedCarbon
        + ", delayedBy="
        + delayedBy
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CarbonReductorOutput that = (CarbonReductorOutput) o;
    return executionDelayed == that.executionDelayed
        && Double.compare(that.originalCarbon, originalCarbon) == 0
        && Double.compare(that.actualCarbon, actualCarbon) == 0
        && Double.compare(that.savedCarbon, savedCarbon) == 0
        && delayedBy == that.delayedBy;
  }

  @Override
  public int hashCode() {
    return Objects.hash(executionDelayed, originalCarbon, actualCarbon, savedCarbon, delayedBy);
  }
}
