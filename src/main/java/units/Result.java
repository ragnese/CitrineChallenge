package units;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Result {
    @JsonProperty("unit_name")
    private final String unitName;
    @JsonProperty("multiplication_factor")
    private final double multiplicationFactor;

    public Result(final String unitName, final double multiplicationFactor) {
        this.unitName = unitName;
        this.multiplicationFactor = multiplicationFactor;
    }

    public String getUnitName() {
        return unitName;
    }

    public double getMultiplicationFactor() {
        return multiplicationFactor;
    }
}
