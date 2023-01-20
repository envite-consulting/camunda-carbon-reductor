package de.envite.greenbpm.carbonreductor.core.domain.model.input.carbonreductormode;

public enum CarbonReductorModes {

    SLA_BASED_MODE("slaBasedConfiguration"),
    TIMESHIFT_WINDOW_ONLY("timeshiftWindowOnly");

    CarbonReductorModes(String mode) {
        this.mode = mode;
    }

    private String mode;

    public String mode() {
        return this.mode;
    }

    public CarbonReductorMode asCarbonReductorMode() {
        return new CarbonReductorMode(this.mode);
    }


}
