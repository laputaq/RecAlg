import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Util {
    public static double[] add(double[] x, double[] y) {
        if (x.length != y.length) {
            System.out.println("The dimensions of two array must be equal!");
            return null;
        }
        double[] z = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            z[i] = x[i] + y[i];
        }
        return z;
    }

    public static double average(double[] a) {
        if (a.length == 0) {
            System.out.println("array is null!");
            return 0;
        }

        double avg = 0;
        for (double v : a)
            avg += v;
        return avg / a.length;
    }

    public static double getSparseMatrix(Map<Integer, Map<Integer, Double>> M, int r, int c) {
        if (M == null) {
            return 0;
        }
        if (M.containsKey(r))
            if (M.get(r).containsKey(c))
                return M.get(r).get(c);
            else return 0;
        else return 0;
    }

    public static double[] add(double[] a, double n) {
        if (a == null)
            return null;
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++)
            c[i] = a[i] + n;
        return c;
    }

    public static double[][] addDiag(double[][] a, double[] b) {
        if (a.length != a[0].length || a.length != b.length)
            return null;
        double[][] c = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++)
            c[i][i] = a[i][i] * b[i];
        return c;
    }

    public static double[][] add(double[][] x, double[][] y) {
        if (x.length != y.length || x[0].length != y[0].length) {
            System.out.println("The dimensions of two matrix must be equal!");
            return null;
        }
        double[][] z = new double[x.length][x[0].length];
        for (int i = 0; i < x.length; i++)
            for (int j = 0; j < x.length; j++)
                z[i][j] = x[i][j] + y[i][j];
        return z;
    }

    public static double[] sub(double[] x, double[] y) {
        if (x.length != y.length) {
            System.out.println("The dimensions of two array must be equal!");
            return null;
        }
        double[] z = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            z[i] = x[i] - y[i];
        }
        return z;
    }

    public static double[] divid(double[] x, double a) {
        double[] z = new double[x.length];
        for (int i = 0; i < x.length; i++)
            z[i] = x[i] / a;
        return z;
    }

    public static double[][] outer(double[] x, double[] y) {
        double[][] z = new double[x.length][y.length];
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < y.length; j++)
                z[i][j] = x[i] * y[j];
        }
        return z;
    }

    public static void print(double[] x) {
        for (double i : x)
            System.out.print(i + " ");
        System.out.println();
    }

    public static void print(double[][] x) {
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[i].length; j++) {
                System.out.print(x[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("------------------------\n");
    }

    public static void print(int[] x) {
        for (int i = 0; i < x.length; i++)
            System.out.print(x[i] + " ");
        System.out.println();
    }

    public static double[][] sliceCol(double[][] a, int[] index) {
        int i;
        for (i = 0; i < index.length; i++) {
            if (index[i] > a[0].length) {
                System.out.println("index out of bounds");
                return null;
            }
        }
        double[][] c = new double[a.length][index.length];
        for (int j = 0; j < index.length; j++) {
            for (i = 0; i < a.length; i++)
                c[i][j] = a[i][index[j]];
        }
        return c;
    }

    public static void print(int[][] x) {
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[i].length; j++) {
                System.out.print(x[i][j] + " ");
            }
            System.out.println();
        }

    }

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

    static int[] descSort(double[] a) {
        Integer[] indexes = new Integer[a.length];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
        Arrays.sort(indexes, (Integer i1, Integer i2) -> Double.compare(a[i2], a[i1]));
        return asArray(indexes);
    }

    private static int[] asArray(Integer[] a) {
        int[] b = new int[a.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = a[i];
        }
        return b;
    }

    public static double[][] multiply(double[][] A, double n) {
        double[][] Z = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                Z[i][j] = A[i][j] * n;
        return Z;
    }

    public static double[] multiply(double[][] a, double[] b) {
        if (a[0].length != b.length) {
            System.out.println("The dimensions of matrix and vector must be equal!");
            return null;
        }
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a[i].length; j++) {
                c[i] += a[i][j] * b[j];
            }
        return c;
    }

    public static double[] multiply(double[] a, double[][] B) {
        if (a.length != B.length) {
            System.out.println("deminsion error");
            return null;
        }
        double[] C = new double[B[0].length];
        for (int j = 0; j < C.length; j++)
            for (int i = 0; i < a.length; i++)
                C[j] += a[i] * B[i][j];
        return C;
    }

    public static double[][] trans(double[] a) {
        double[][] A = new double[a.length][1];
        for (int i = 0; i < a.length; i++)
            A[i][0] = a[i];
        return A;
    }

    public static double[][] multiply(double[][] a, double[][] b) {

        if (a[0].length != b.length) {
            System.out.println("dimension error!");
            return null;
        }
        int R = a.length;
        int K = a[0].length;
        int C = b[0].length;
        double[][] z = new double[R][C];
        for (int i = 0; i < R; i++)
            for (int j = 0; j < C; j++) {
                double sum = 0;
                for (int k = 0; k < K; k++) {
                    sum += a[i][k] * b[k][j];
                }
                z[i][j] = sum;
            }
        return z;
    }

    public static double[][] trans(double[][] A) {
        int r = A.length;
        int c = A[0].length;
        double[][] M = new double[c][r];
        for (int i = 0; i < c; i++) {
            for (int j = 0; j < r; j++) {
                M[i][j] = A[j][i];
            }
        }
        return M;
    }


    public static boolean equal(int[] a, int[] b) {
        if (a.length != b.length)
            return false;
        int c = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == b[i]) {
                c++;
            }
        }
        return c == a.length;
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

    public static void norm(double[] a) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i];
        }
        for (int i = 0; i < a.length; i++) {
            a[i] = a[i] / sum;
        }
    }

    public static void setCol(double[][] a, int col, double[] b) {
        if (col > a[0].length - 1 || a.length != b.length) {
            System.out.println("col is out of bounds or the dimensons of two matrix are not equal!");
            return;
        }
        for (int i = 0; i < a.length; i++) {
            a[i][col] = b[i];
        }
    }

    public static double[][] multiplyDiag(double[][] a, double[] b) {
        if (a[0].length != b.length) {
            System.out.println("The dimensions of matrix a and diag matrix must be equal!");
            return null;
        }
        double[][] c = new double[a.length][a[0].length];
        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < c[i].length; j++) {
                c[i][j] = a[i][j] * b[j];
            }
        }
        return c;
    }

    public static int[] pickNRandom(int[] array, int n) {

        List<Integer> list = new ArrayList<Integer>(array.length);
        for (int i : array)
            list.add(i);
        Collections.shuffle(list);

        int[] answer = new int[n];
        for (int i = 0; i < n; i++)
            answer[i] = list.get(i);
        Arrays.sort(answer);

        return answer;

    }

    public static int[] argSort(double[] array, int k) {
        if (array != null && array.length > 1) {
            //创建索引数组
            int n = array.length;
            int[] index = new int[n];
            //初始化索引数组
            int i, j;
            for (i = 0; i < n; i++)
                index[i] = i;
            //类似于插入排序，在插入比较的过程中不断地修改index数组
            for (i = 0; i < n; i++) {
                j = i;
                while (j > 0) {
                    if (array[index[j]] > array[index[j - 1]]) {
                        int t = index[j];
                        index[j] = index[j - 1];
                        index[j - 1] = t;
                    } else
                        break;
                    j--;
                }
            }
            int[] topk = new int[k];
            for (i = 0; i < k; i++)
                topk[i] = index[i];
            return topk;
        } else return null;
    }

    public static double log2(double a) {
        return Math.log(a) / Math.log(2);
    }

    public static double ndcg(int[] list, List<Integer> truth) {
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
