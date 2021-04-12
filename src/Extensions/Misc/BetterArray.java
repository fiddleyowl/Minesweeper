package Extensions.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BetterArray {
    public static ArrayList<Boolean> createArrayList(boolean[] array) {
        Boolean[] i = new Boolean[array.length];
        for (int j=0;j<array.length;j++) {
            i[j] = array[j];
        }
        List<Boolean> k = Arrays.asList(i);
        return new ArrayList<>(k);
    }

    public static ArrayList<Byte> createArrayList(byte[] array) {
        Byte[] i = new Byte[array.length];
        for (int j=0;j<array.length;j++) {
            i[j] = array[j];
        }
        List<Byte> k = Arrays.asList(i);
        return new ArrayList<>(k);
    }

    public static ArrayList<Short> createArrayList(short[] array) {
        Short[] i = new Short[array.length];
        for (int j=0;j<array.length;j++) {
            i[j] = array[j];
        }
        List<Short> k = Arrays.asList(i);
        return new ArrayList<>(k);
    }

    public static ArrayList<Integer> createArrayList(int[] array) {
        Integer[] i = new Integer[array.length];
        for (int j=0;j<array.length;j++) {
            i[j] = array[j];
        }
        List<Integer> k = Arrays.asList(i);
        return new ArrayList<>(k);
    }

    public static ArrayList<Long> createArrayList(long[] array) {
        Long[] i = new Long[array.length];
        for (int j=0;j<array.length;j++) {
            i[j] = array[j];
        }
        List<Long> k = Arrays.asList(i);
        return new ArrayList<>(k);
    }

    public static ArrayList<Float> createArrayList(float[] array) {
        Float[] i = new Float[array.length];
        for (int j=0;j<array.length;j++) {
            i[j] = array[j];
        }
        List<Float> k = Arrays.asList(i);
        return new ArrayList<>(k);
    }

    public static ArrayList<Double> createArrayList(double[] array) {
        Double[] i = new Double[array.length];
        for (int j=0;j<array.length;j++) {
            i[j] = array[j];
        }
        List<Double> k = Arrays.asList(i);
        return new ArrayList<>(k);
    }

    public static ArrayList<Character> createArrayList(char[] array) {
        Character[] i = new Character[array.length];
        for (int j=0;j<array.length;j++) {
            i[j] = array[j];
        }
        List<Character> k = Arrays.asList(i);
        return new ArrayList<>(k);
    }

    public static ArrayList<String> createArrayList(String[] array) {
        List<String> k = Arrays.asList(array);
        return new ArrayList<>(k);
    }
}
