import java.util.ArrayList;
import java.util.HashSet;

class Evaluation {

    static void evaluate(String name, ArrayList<Integer>[] testset, int[][] reclist, int topn) {
        int hit = 0, sum = 0;
        for (int g = 0; g < testset.length; g++) {if (Input.te_num == 71) {return;}
            HashSet<Integer> set = new HashSet<>(testset[g]);
            sum += set.size();
            for (int v : reclist[g]) {
                if (set.contains(v))  {
                    hit++;
                }
            }
        }
        double p = 1.0 * hit / (Input.g_num * topn);
        double r = 1.0 * hit / sum;
        double f1 = 1.0 * (2 * p * r) / (p + r);
        System.out.print("hit: " + hit);
        System.out.println("\tsum: " + sum);
        System.out.print("Precision: " + p);
        System.out.print("\tRecall: " + r);
        System.out.println("\tF1: " + f1);
    }
}
