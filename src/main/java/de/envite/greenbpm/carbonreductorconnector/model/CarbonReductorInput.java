package de.envite.greenbpm.carbonreductorconnector.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CarbonReductorInput {

  // TODO regex
  @NotEmpty private String timerDuration;
  @NotEmpty private String location;
  @NotEmpty private String timestamp;
}
