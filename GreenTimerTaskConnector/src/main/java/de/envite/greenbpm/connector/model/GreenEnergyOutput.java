/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.envite.greenbpm.connector.model;

import java.util.Objects;
import lombok.Builder;

@Builder
public class GreenEnergyOutput {

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
    GreenEnergyOutput that = (GreenEnergyOutput) o;
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
