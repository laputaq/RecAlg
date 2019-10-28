import java.util.ArrayList;
import java.util.Calendar;

public class Evaluation {

    private static int[][] locTimes = Dataset.getLocTime();

    private static Calendar calendar = Calendar.getInstance();

    private static String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    private static int getWeek(int time) {
        calendar.setTimeInMillis(1000L * time);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    private static boolean isSameEvent(int e1, int e2) {
        return locTimes[e1][0] == locTimes[e2][0] && getWeek(locTimes[e1][1]) == getWeek(locTimes[e2][1]);
    }

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
                for (int e1 : testset[g]) {
                    for (int e2 : reclist[g]) {
                        if (isSameEvent(e1, e2)) {
                            truth++;
                            if (cnt < topn) hit++;
                        }
                        cnt++;
                    }
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
