package units;

enum Operator {
    MULTIPLY,
    DIVIDE,
    LEFT_PAREN,
    RIGHT_PAREN;

    static Operator fromChar(final char c) throws ConversionException {
        switch (c) {
            case ('*'):
                return MULTIPLY;
            case ('/'):
                return DIVIDE;
            case ('('):
                return LEFT_PAREN;
            case (')'):
                return RIGHT_PAREN;
            default:
                throw new ConversionException();
        }
    }

    static boolean isOperator(final char c) {
        switch (c) {
            case('*'):
            case('/'):
            case('('):
            case(')'):
                return true;
            default:
                return false;
        }
    }
}
