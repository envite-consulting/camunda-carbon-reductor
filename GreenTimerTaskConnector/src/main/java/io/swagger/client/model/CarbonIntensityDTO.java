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
/*
 * CarbonAware.WebApi
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 1.0
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.client.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import org.threeten.bp.OffsetDateTime;

/** CarbonIntensityDTO */
@javax.annotation.Generated(
    value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
    date = "2022-10-17T16:13:58.157Z[GMT]")
public class CarbonIntensityDTO {
  @SerializedName("location")
  private String location = null;

  @SerializedName("startTime")
  private OffsetDateTime startTime = null;

  @SerializedName("endTime")
  private OffsetDateTime endTime = null;

  @SerializedName("carbonIntensity")
  private Double carbonIntensity = null;

  public CarbonIntensityDTO location(String location) {
    this.location = location;
    return this;
  }

  /**
   * the location name where workflow is run
   *
   * @return location
   */
  @Schema(example = "eastus", description = "the location name where workflow is run")
  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public CarbonIntensityDTO startTime(OffsetDateTime startTime) {
    this.startTime = startTime;
    return this;
  }

  /**
   * the time at which the workflow we are measuring carbon intensity for started
   *
   * @return startTime
   */
  @Schema(
      example = "2022-03-01T15:30Z",
      description = "the time at which the workflow we are measuring carbon intensity for started")
  public OffsetDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(OffsetDateTime startTime) {
    this.startTime = startTime;
  }

  public CarbonIntensityDTO endTime(OffsetDateTime endTime) {
    this.endTime = endTime;
    return this;
  }

  /**
   * the time at which the workflow we are measuring carbon intensity for ended
   *
   * @return endTime
   */
  @Schema(
      example = "2022-03-01T18:30Z",
      description = "the time at which the workflow we are measuring carbon intensity for ended")
  public OffsetDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(OffsetDateTime endTime) {
    this.endTime = endTime;
  }

  public CarbonIntensityDTO carbonIntensity(Double carbonIntensity) {
    this.carbonIntensity = carbonIntensity;
    return this;
  }

  /**
   * Value of the marginal carbon intensity in grams per kilowatt-hour.
   *
   * @return carbonIntensity
   */
  @Schema(
      example = "345.434",
      description = "Value of the marginal carbon intensity in grams per kilowatt-hour.")
  public Double getCarbonIntensity() {
    return carbonIntensity;
  }

  public void setCarbonIntensity(Double carbonIntensity) {
    this.carbonIntensity = carbonIntensity;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CarbonIntensityDTO carbonIntensityDTO = (CarbonIntensityDTO) o;
    return Objects.equals(this.location, carbonIntensityDTO.location)
        && Objects.equals(this.startTime, carbonIntensityDTO.startTime)
        && Objects.equals(this.endTime, carbonIntensityDTO.endTime)
        && Objects.equals(this.carbonIntensity, carbonIntensityDTO.carbonIntensity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(location, startTime, endTime, carbonIntensity);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CarbonIntensityDTO {\n");

    sb.append("    location: ").append(toIndentedString(location)).append("\n");
    sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
    sb.append("    endTime: ").append(toIndentedString(endTime)).append("\n");
    sb.append("    carbonIntensity: ").append(toIndentedString(carbonIntensity)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
