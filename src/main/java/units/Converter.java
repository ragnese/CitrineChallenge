package units;

import java.util.*;

import static units.Operator.LEFT_PAREN;

public final class Converter {
    private static final Map<String, UnitConversion> map;
    static {
        map = new HashMap<>(); // Number of unit conversions implemented = 16, which happens to be the default capacity!

        final UnitConversion secToSec = new UnitConversion("s", 1);
        final UnitConversion radToRad = new UnitConversion("rad", 1);
        final UnitConversion meterToMeter = new UnitConversion("m", 1);
        final UnitConversion kgToKg = new UnitConversion("kg", 1);
        final UnitConversion minToSec = new UnitConversion("s", 60);
        final UnitConversion hourToSec = new UnitConversion("s", 3600);
        final UnitConversion dayToSec = new UnitConversion("s", 84600);
        final UnitConversion degToRad = new UnitConversion("rad", Math.PI/180.0);
        final UnitConversion minuteToRad = new UnitConversion("rad", Math.PI/10800.0);
        final UnitConversion secondToRad = new UnitConversion("rad", Math.PI/648000.0);
        final UnitConversion hecToM2 = new UnitConversion("m*m", 10000);
        final UnitConversion litreToM3 = new UnitConversion("m*m*m", 0.001);
        final UnitConversion tonToM3 = new UnitConversion("kg", 1000);

        map.put("second", secToSec);
        map.put("s", secToSec);

        map.put("rad", radToRad);

        map.put("m", meterToMeter);
        map.put("meter", meterToMeter);

        map.put("kilogram", kgToKg);
        map.put("kg", kgToKg);

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
        map.put("ha", hecToM2);

        map.put("litre", litreToM3);
        map.put("L", litreToM3);

        map.put("tonne", tonToM3);
        map.put("t", tonToM3);
    }

    public static Result ConvertToSI(final String expression) throws ConversionException {
        final PostfixStackAndUnitString intermediate = parseExpression(expression);

        final double conversionFactor = evaluatePostfixStack(intermediate.stack);

        return new Result(intermediate.units, conversionFactor);
    }

    private static PostfixStackAndUnitString parseExpression(final String expression) throws ConversionException {
        // See: Dijkstra's shunting-yard algorithm

        // Object = Double | Operator
        final Deque<Object> postfixStack = new ArrayDeque<>();
        final Deque<Operator> operators = new ArrayDeque<>();

        final StringBuilder unitsBuilder = new StringBuilder();

        final StringBuilder tokenBuffer = new StringBuilder();
        for (int i = 0; i < expression.length(); ++i) {
            switch (expression.charAt(i)) {
                case '(':
                    unitsBuilder.append(expression.charAt(i));
                    operators.push(LEFT_PAREN);
                    break;
                case ')':
                    unitsBuilder.append(expression.charAt(i));
                    while (operators.size() > 0 && operators.peek() != LEFT_PAREN) {
                        postfixStack.push(operators.pop());
                    }

                    if (operators.peek() != LEFT_PAREN) {
                        throw new ConversionException();
                    } else {
                        operators.pop();
                    }

                    break;
                case '*':
                case '/':
                    unitsBuilder.append(expression.charAt(i));
                    while (operators.size() > 0 && operators.peek() != LEFT_PAREN) {
                        postfixStack.push(operators.pop());
                    }
                    operators.push(Operator.fromChar(expression.charAt(i)));
                    break;
                default:
                    tokenBuffer.append(expression.charAt(i));
                    // If we've reached end of expression or the next char is an operator, push token to postfixStack,
                    // add SI unit to the unitsBuilder, and clear tokenBuffer for next token
                    if (i == expression.length() - 1 || Operator.isOperator(expression.charAt(i + 1))) {
                        final UnitConversion conversion = map.get(tokenBuffer.toString());
                        if (conversion == null) {
                            throw new ConversionException();
                        }

                        unitsBuilder.append(conversion.getNewSymbol());

                        postfixStack.push(conversion.getConversionFactor());

                        tokenBuffer.delete(0, tokenBuffer.length());
                    }
            }
        }

        while (operators.size() > 0) {
            postfixStack.push(operators.pop());
        }

        return new PostfixStackAndUnitString(postfixStack, unitsBuilder.toString());
    }

    private static double evaluatePostfixStack(final Deque<Object> stack) throws ConversionException {
        final Deque<Double> result = new ArrayDeque<>();

        final Iterator<Object> iterator = stack.descendingIterator();
        while (iterator.hasNext()) {
            final Object token = iterator.next();
            if (token instanceof Double) {
                result.push((Double) token);
            } else {
                final Double op1;
                final Double op2;
                try {
                    op2 = result.pop();
                    op1 = result.pop();
                } catch (final NoSuchElementException e) {
                    throw new ConversionException();
                }


                switch ((Operator) token) {
                    case MULTIPLY:
                        result.push(op1 * op2);
                        break;
                    case DIVIDE:
                        result.push(op1 / op2);
                        break;
                    default:
                        throw new ConversionException();
                }
            }
        }

        return result.pop();
    }

    private static class PostfixStackAndUnitString {
        Deque<Object> stack;
        String units;

        PostfixStackAndUnitString(final Deque<Object> stack, final String units) {
            this.stack = stack;
            this.units = units;
        }
    }
}
