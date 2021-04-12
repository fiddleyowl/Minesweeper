package Extensions.TypeCasting;

public class CastFloat {
    public static float Float(byte i) { return i; }

    public static float Float(short i) { return i; }

    public static float Float(int i) { return (float) i; }

    public static float Float(long i) { return (float) i; }

    public static float Float(float i) { return i; }

    public static float Float(double i) { return (float) i; }

    public static float Float(String i) { return Float.parseFloat(i); }
}
