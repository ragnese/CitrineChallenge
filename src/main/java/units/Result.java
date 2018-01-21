package units;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public final class Result {
    private static final NumberFormat formatter = new DecimalFormat("#0.00000000000000");
    private final String unitName;
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

    public String toJson() {
        return "{\"unit_name\":\"" + unitName + "\"," +
                "\"multiplication_factor\":" + formatter.format(multiplicationFactor) + "}";
    }
}
