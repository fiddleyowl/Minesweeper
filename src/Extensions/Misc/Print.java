package Extensions.Misc;

import static Extensions.TypeCasting.CastString.*;

public class Print {
    public static void print(boolean i) { System.out.println(String(i)); }

    public static void print(byte i) { System.out.println(String(i)); }

    public static void print(short i) { System.out.println(String(i)); }

    public static void print(int i) { System.out.println(String(i)); }

    public static void print(long i) { System.out.println(String(i)); }

    public static void print(float i) { System.out.println(String(i)); }

    public static void print(double i) { System.out.println(String(i)); }

    public static void print(char i) { System.out.println(String(i)); }

    public static void print(String i) { System.out.println(String(i)); }

    public static void print(Object i) { System.out.println(String(i)); }

    public static void print(boolean[] i) { System.out.println(String(i)); }

    public static void print(byte[] i) { System.out.println(String(i)); }

    public static void print(short[] i) { System.out.println(String(i)); }

    public static void print(int[] i) { System.out.println(String(i)); }

    public static void print(long[] i) { System.out.println(String(i)); }

    public static void print(float[] i) { System.out.println(String(i)); }

    public static void print(double[] i) { System.out.println(String(i)); }

    public static void print(char[] i) { System.out.println(String(i)); }

    public static void print(Character[] i) { System.out.println(String(i)); }

    public static void print(String[] i) { System.out.println(String(i)); }

    public static void print(Object[] i) { System.out.println(String(i)); }

    public static void print(String i, Object... args) { System.out.printf(i,args); }

}
