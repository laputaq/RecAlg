import java.util.ArrayList;

public class Evaluation {
    static void evaluate(ArrayList<Integer>[] testset, int[][] reclist, String metric, int topn) {
        if ("Recall".equals(metric)) {
            int hit = 0;
            int truth = 0;
            int recall = 0;
            int precision = 0;
            int cnt;
            for (int g = 0; g < testset.length; g++) {
                recall += testset[g].size();
                precision += topn;
                cnt = 0;
                for (int e : reclist[g]) {
                    if (testset[g].contains(e)) {
                        truth++;
                        if (cnt < topn) hit++;
                    }
                    cnt++;
                }
            }
            Dataset.saveEvaluation(topn, hit, truth, recall, precision);
        } else {
            int count = 0;
            double ndcg = 0;
            for (int g = 0; g < testset.length; g++) {
                double tmp = Util.ndcg(reclist[g], testset[g]);
                if (tmp > 0) {
                    ndcg += tmp;
                    count++;
                }
            }
            System.out.println("nDCG=" + ndcg / count);
        }
    }
}
