package Extensions.TypeCasting;

import static Extensions.TypeCasting.CastString.String;

public class CastInt16 {
    public static short Int16(byte i) { return i; }

    public static short Int16(short i) { return i; }

    public static short Int16(int i) { return (short) i; }

    public static short Int16(long i) { return (short) i; }

    /**
     * Creates a short from the given floating-point value.
     * Be very careful about this operation. Precision may lose.
     */
    public static short Int16(float i) {
        return (short) Double.parseDouble(String(i));
    }

    /**
     * Creates a short from the given floating-point value.
     * Be very careful about this operation. Precision may lose.
     */
    public static short Int16(double i) {
        return (short) Double.parseDouble(String(i));
    }

    /**
     * Parses the string argument as a signed decimal short.
     */
    public static short Int16(String i) {
        return Short.parseShort(i);
    }
}
