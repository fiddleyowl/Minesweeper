package Extensions.TypeCasting;

import static Extensions.TypeCasting.CastString.String;

public class CastInt {
    public static int Int(byte i) { return i; }

    public static int Int(short i) { return i; }

    public static int Int(int i) { return i; }

    public static int Int(long i) { return (int) i; }

    /**
     * Creates an int from the given floating-point value.
     * Be very careful about this operation. Precision may lose.
     */
    public static int Int(float i) {
        return (int) Double.parseDouble(String(i));
    }

    /**
     * Creates an int from the given floating-point value.
     * Be very careful about this operation. Precision may lose.
     */
    public static int Int(double i) {
        return (int) Double.parseDouble(String(i));
    }

    /**
     * Parses the string argument as a signed decimal int.
     */
    public static int Int(String i) throws NumberFormatException {
        return Integer.parseInt(i);
    }
}
