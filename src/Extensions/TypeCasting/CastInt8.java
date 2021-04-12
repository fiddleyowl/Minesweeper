package Extensions.TypeCasting;

import static Extensions.TypeCasting.CastString.String;

public class CastInt8 {
    public static byte Int8(byte i) { return i; }

    public static byte Int8(short i) { return (byte) i; }

    public static byte Int8(int i) { return (byte) i; }

    public static byte Int8(long i) { return (byte) i; }

    /**
     * Creates a byte from the given floating-point value.
     * Be very careful about this operation. Precision may lose.
     */
    public static byte Int8(float i) {
        return (byte) Double.parseDouble(String(i));
    }

    /**
     * Creates a byte from the given floating-point value.
     * Be very careful about this operation. Precision may lose.
     */
    public static byte Int8(double i) {
        return (byte) Double.parseDouble(String(i));
    }

    /**
     * Parses the string argument as a signed decimal byte.
     */
    public static byte Int8(String i) {
        return Byte.parseByte(i);
    }
}
