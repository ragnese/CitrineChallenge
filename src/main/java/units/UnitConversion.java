package units;

public final class UnitConversion {
    private final String newSymbol;
    private final double conversionFactor;

    public UnitConversion(final String newSymbol, final double conversionFactor) {
        this.newSymbol = newSymbol;
        this.conversionFactor = conversionFactor;
    }

    public String getNewSymbol() {
        return newSymbol;
    }

    public double getConversionFactor() {
        return conversionFactor;
    }
}
