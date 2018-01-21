package units;

final class UnitConversion {
    private final String newSymbol;
    private final double conversionFactor;

    UnitConversion(final String newSymbol, final double conversionFactor) {
        this.newSymbol = newSymbol;
        this.conversionFactor = conversionFactor;
    }

    String getNewSymbol() {
        return newSymbol;
    }

    double getConversionFactor() {
        return conversionFactor;
    }
}
