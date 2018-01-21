import org.junit.Test;
import units.ConversionException;
import units.Converter;
import units.Result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public final class UnitsHandlerTest {

    @Test
    public void checkSimple() {
        Result result;
        try {
            result = Converter.ConvertToSI("min");
        } catch (final ConversionException e) {
            fail();
            return;
        }
        assertEquals(60.0, result.getMultiplicationFactor(), 1e-15);
        assertEquals("s", result.getUnitName());

        try {
            result = Converter.ConvertToSI("minute");
        } catch (final ConversionException e) {
            fail();
            return;
        }
        assertEquals(60.0, result.getMultiplicationFactor(), 1e-15);
        assertEquals("s", result.getUnitName());

        try {
            result = Converter.ConvertToSI("'");
        } catch (final ConversionException e) {
            fail();
            return;
        }
        assertEquals(Math.PI/10800.0, result.getMultiplicationFactor(), 1e-15);
        assertEquals("rad", result.getUnitName());
    }

    @Test
    public void checkParentheses() {
        Result result;
        try {
            result = Converter.ConvertToSI("(min)");
        } catch (final ConversionException e) {
            fail();
            return;
        }
        assertEquals(60.0, result.getMultiplicationFactor(), 1e-15);
        assertEquals("(s)", result.getUnitName());

        try {
            result = Converter.ConvertToSI("((minute))");
        } catch (final ConversionException e) {
            fail();
            return;
        }
        assertEquals(60.0, result.getMultiplicationFactor(), 1e-15);
        assertEquals("((s))", result.getUnitName());

        try {
            result = Converter.ConvertToSI("(')");
        } catch (final ConversionException e) {
            fail();
            return;
        }
        assertEquals(Math.PI/10800.0, result.getMultiplicationFactor(), 1e-15);
        assertEquals("(rad)", result.getUnitName());
    }

    @Test
    public void checkCompound() {
        Result result;
        try {
            result = Converter.ConvertToSI("minute*minute");
        } catch (final ConversionException e) {
            fail();
            return;
        }
        assertEquals(3600.0, result.getMultiplicationFactor(), 1e-15);
        assertEquals("s*s", result.getUnitName());

        try {
            result = Converter.ConvertToSI("min/minute");
        } catch (final ConversionException e) {
            fail();
            return;
        }
        assertEquals(1.0, result.getMultiplicationFactor(), 1e-15);
        assertEquals("s/s", result.getUnitName());

        try {
            result = Converter.ConvertToSI("degree/minute");
        } catch (final ConversionException e) {
            fail();
            return;
        }
        assertEquals(0.00029088820867, result.getMultiplicationFactor(), 1e-15);
        assertEquals("rad/s", result.getUnitName());
    }
}
