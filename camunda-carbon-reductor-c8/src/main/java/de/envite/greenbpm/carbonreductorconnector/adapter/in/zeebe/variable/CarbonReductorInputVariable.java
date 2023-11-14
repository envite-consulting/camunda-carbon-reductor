package de.envite.greenbpm.carbonreductorconnector.adapter.in.zeebe.variable;

import lombok.Data;

@Data
public class CarbonReductorInputVariable {

    private String location;
    private String milestone;
    private String remainingProcessDuration;
    private String maximumProcessDuration;
    private String timeshiftWindow;
    private String errorHandling;
    private boolean measurementOnly;
    private boolean thresholdEnabled;
    private Float thresholdValue;

}
