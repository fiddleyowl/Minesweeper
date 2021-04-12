package Extensions.TypeCasting;

import static Extensions.TypeCasting.CastString.String;

public class CastInt32 {
    public static int Int32(byte i) { return i; }

    public static int Int32(short i) { return i; }

    public static int Int32(int i) { return i; }

    public static int Int32(long i) { return (int) i; }

    /**
     * Creates an int from the given floating-point value.
     * Be very careful about this operation. Precision may lose.
     */
    public static int Int32(float i) {
        return (int) Double.parseDouble(String(i));
    }

    /**
     * Creates an int from the given floating-point value.
     * Be very careful about this operation. Precision may lose.
     */
    public static int Int32(double i) {
        return (int) Double.parseDouble(String(i));
    }

    /**
     * Parses the string argument as a signed decimal int.
     */
    public static int Int32(String i) {
        return Integer.parseInt(i);
    }
}
