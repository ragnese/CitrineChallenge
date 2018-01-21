package units;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static units.Operator.PAREN;

public final class Converter {
    private static final Map<String, UnitConversion> map;
    static {
        map = new HashMap<>(); // Number of unit conversions implemented = 16, which happens to be the default capacity!

        final UnitConversion minToSec = new UnitConversion("s", 60);
        final UnitConversion hourToSec = new UnitConversion("s", 3600);
        final UnitConversion dayToSec = new UnitConversion("s", 84600);
        final UnitConversion degToRad = new UnitConversion("rad", Math.PI/180.0);
        final UnitConversion minuteToRad = new UnitConversion("rad", Math.PI/10800.0);
        final UnitConversion secondToRad = new UnitConversion("rad", Math.PI/648000.0);
        final UnitConversion hecToM2 = new UnitConversion("m*m", 10000);
        final UnitConversion litreToM3 = new UnitConversion("m*m*m", 0.001);
        final UnitConversion tonToM3 = new UnitConversion("kg", 1000);

        map.put("minute", minToSec);
        map.put("min", minToSec);

        map.put("hour", hourToSec);
        map.put("h", hourToSec);

        map.put("day", dayToSec);
        map.put("d", dayToSec);

        map.put("degree", degToRad);
        map.put("\u00BA", degToRad);

        // Don't reuse "minute"
        map.put("'", minuteToRad);

        // Don't reuse "second"
        map.put("\"", secondToRad);

        map.put("hectare", hecToM2);
        map.put("a", hecToM2);

        map.put("litre", litreToM3);
        map.put("L", litreToM3);

        map.put("tonne", tonToM3);
        map.put("t", tonToM3);
    }

    public static Result ConvertToSI(final String expression) throws ConversionException {
        // Object = Double | Operator
        final Deque<Object> output = new ArrayDeque<>();
        final Deque<Operator> operators = new ArrayDeque<>();

        final StringBuilder resultUnitName = new StringBuilder();

        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < expression.length(); ++i) {
            switch (expression.charAt(i)) {
                case '(':
                    popFromBuilderToStack(output, resultUnitName, buffer);
                    resultUnitName.append(expression.charAt(i));
                    operators.push(PAREN);
                    break;
                case ')':
                    popFromBuilderToStack(output, resultUnitName, buffer);
                    resultUnitName.append(expression.charAt(i));
                    while (operators.size() > 0 && operators.peek() != PAREN) {
                        output.push(operators.pop());
                    }

                    if (operators.peek() != PAREN) {
                        throw new ConversionException();
                    } else {
                        operators.pop();
                    }

                    break;
                case '*':
                case '/':
                    popFromBuilderToStack(output, resultUnitName, buffer);
                    resultUnitName.append(expression.charAt(i));
                    while (operators.size() > 0 && operators.peek() != PAREN) {
                        output.push(operators.pop());
                    }
                    operators.push(Operator.fromChar(expression.charAt(i)));
                    break;
                default:
                    buffer.append(expression.charAt(i));
                    if (i == expression.length() - 1) {
                        popFromBuilderToStack(output, resultUnitName, buffer);
                    }
            }
        }

        while (operators.size() > 0) {
            output.push(operators.pop());
        }

        // End of shunting yard algo

        final Deque<Double> resultStack = new ArrayDeque<>();
        final Iterator<Object> iterator = output.descendingIterator();
        while (iterator.hasNext()) {
            final Object token = iterator.next();
            if (token instanceof Double) {
                resultStack.push((Double) token);
            } else {
                // TODO error handling
                final Double op1 = resultStack.pop();
                final Double op2 = resultStack.pop();

                switch ((Operator) token) {
                    case MULTIPLY:
                        resultStack.push(op1 * op2);
                        break;
                    case DIVIDE:
                        resultStack.push(op1 / op2);
                        break;
                    default:
                        throw new ConversionException();
                }
            }
        }

        final BigDecimal bd = new BigDecimal(resultStack.pop()).setScale(14, RoundingMode.UP);
        final double resultConversionFactor = bd.doubleValue();

        return new Result(resultUnitName.toString(), resultConversionFactor);
    }

    private static void popFromBuilderToStack(final Deque<Object> stack, final StringBuilder outputString, final StringBuilder builder)
            throws ConversionException {
        if (builder.length() == 0) {
            return;
        }

        final UnitConversion conversion = map.get(builder.toString());
        if (conversion == null) {
            throw new ConversionException();
        }

        outputString.append(conversion.getNewSymbol());

        stack.add(conversion.getConversionFactor());
        builder.delete(0, builder.length());
    }
}
