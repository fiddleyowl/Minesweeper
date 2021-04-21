package Extensions.TypeCasting;

import static Extensions.TypeCasting.CastString.String;

public class CastLong {
    public static long Long(byte i) { return (long) i; }

    public static long Long(short i) { return (long) i; }

    public static long Long(int i) { return (long) i; }

    public static long Long(long i) { return (long) i; }

    /**
     * Creates a long from the given floating-point value.
     * Be very careful about this operation. Precision may lose.
     */
    public static long Long(float i) { return (long) Double.parseDouble(String(i)); }

    /**
     * Creates a long from the given floating-point value.
     * Be very careful about this operation. Precision may lose.
     */
    public static long Long(double i) { return (long) Double.parseDouble(String(i)); }

    /**
     * Parses the string argument as a signed decimal long.
     */
    public static long Long(String i) { return Long.parseLong(i); }

    public static long Int64(byte i) { return i; }

    public static long Int64(short i) { return i; }

    public static long Int64(int i) { return i; }

    public static long Int64(long i) { return i; }

    /**
     * Creates a long from the given floating-point value.
     * Be very careful about this operation. Precision may lose.
     */
    public static long Int64(float i) { return (long) Double.parseDouble(String(i)); }

    /**
     * Creates a long from the given floating-point value.
     * Be very careful about this operation. Precision may lose.
     */
    public static long Int64(double i) { return (long) Double.parseDouble(String(i)); }

    /**
     * Parses the string argument as a signed decimal long.
     */
    public static long Int64(String i) { return Long.parseLong(i); }
}
