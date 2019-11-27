import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Util {

    static double[] ravel(double[][] x) {
        int row = x.length;
        int col = x[0].length;
        double[] z = new double[row * col];
        for (int i = 0; i < row; i++) {
            System.arraycopy(x[i], 0, z, col * i, col);
        }
        return z;
    }

    static int[] unravel_index(int index, int r, int c) {
        if (index > r * c) return null;
        int[] z = new int[2];
        z[0] = index / c;
        z[1] = index - c * z[0];
        return z;
    }

    static int[] descSort(List<Integer> candlist, double[] a) {
        Integer[] indexes = new Integer[a.length];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
        Arrays.sort(indexes, (Integer i1, Integer i2) -> Double.compare(a[i2], a[i1]));
        return asArray(candlist, indexes);
    }

    private static int[] asArray(List<Integer> candlist, Integer[] a) {
        int[] b = new int[a.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = candlist.get(a[i]);
        }
        return b;
    }

    static double[] newnorm(double[] a) {
        double[] b = new double[a.length];
        double sum = 0;
        for (double item : a) {
            sum += item;
        }
        for (int i = 0; i < a.length; i++) {
            b[i] = a[i] / sum;
        }
        return b;
    }

    static double log2(double a) {
        return Math.log(a) / Math.log(2);
    }

    static double ndcg(int[] list, List<Integer> truth) {
        double dcg = 0;
        int i;
        double[] rel = new double[list.length];
        for (i = 0; i < list.length; i++) {
            if (truth.contains(list[i]))
                rel[i] = 1;
        }
        for (i = 0; i < rel.length; i++) {
            dcg += (Math.pow(2, rel[i]) - 1) / log2(i + 2);
        }
        if (dcg == 0)
            return dcg;
//		System.out.println(dcg);
        Arrays.sort(rel);
        double[] irel = new double[rel.length];
        for (i = 0; i < rel.length; i++)
            irel[i] = rel[rel.length - i - 1];
        double idcg = 0;
//		print(irel);
        for (i = 0; i < irel.length; i++) {
            idcg += (Math.pow(2, irel[i]) - 1) / log2(i + 2);
        }
//		System.out.println(idcg);
        return dcg / idcg;
    }

    public static void main(String[] args) {
        System.out.println(1.856 / 2.131);
        int[] list = {1, 2, 3, 4, 5, 6};
        int[] truth = {1, 3, 6};
        List<Integer> T = new ArrayList<Integer>();
        for (int i = 0; i < truth.length; i++) {
            T.add(truth[i]);
        }
        System.out.println(Util.ndcg(list, T));
    }
}
