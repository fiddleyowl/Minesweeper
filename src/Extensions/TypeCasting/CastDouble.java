package Extensions.TypeCasting;

public class CastDouble {
    public static double Double(byte i) {
        return i;
    }

    public static double Double(short i) {
        return i;
    }

    public static double Double(int i) {
        return i;
    }

    public static double Double(long i) {
        return (double) i;
    }

    public static double Double(float i) {
        return i;
    }

    public static double Double(double i) {
        return i;
    }

    public static double Double(String i) {
        return Double.parseDouble(i);
    }
}
