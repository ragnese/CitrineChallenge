package units;

public enum Operator {
    MULTIPLY,
    DIVIDE,
    PAREN;
    // No right paren because they never get put on the stack

    public static Operator fromChar(final char c) throws ConversionException {
        switch (c) {
            case ('*'):
                return MULTIPLY;
            case ('/'):
                return DIVIDE;
            case ('('):
                return PAREN;
            default:
                throw new ConversionException();
        }
    }
}
