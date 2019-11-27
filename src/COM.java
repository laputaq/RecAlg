import java.util.*;

public class COM {

    static class CModel {
        double[][] lambda;
        double[][] ui;
        double[][] zi;
        double[][] zu;
        double[][] gz;
    }

    private int U;
    private int I;
    private int Z;
    private int G;
    private int C;
    private double alpha;
    private double beta;
    private double eta;
    private double gamma;
    private double gamma_t;
    private double rho;

    private int[][] nzi;    //物品i从主题z中提取的次数，不包括第j个用户-物品对
    private int[] nziSum;
    private int[][] ngz;    //主题z被分配给g组的次数，不包括第j个用户-物品对
    private int[] ngzSum;
    private int[][] nzu;    //用户u从主题z中抽取的次数，不包括第j个用户-物品对
    private int[] nzuSum;
    private int[][] nui;    //物品i从用户u中提取的次数，不包括第j个用户-物品对
    private int[] nuiSum;
    private int[][] nuc;    //开关c从用户u中提取的次数，不包括第j个用户-物品对
    private int[] Cg;
    private int[] Zg;
    private CModel model;
    private ArrayList<Integer>[] trainset;
    private ArrayList<Integer>[] testset;
    private ArrayList<Integer>[] groups;
    private static String name = "COM";

    public static void main(String[] args) {
        int c = 2, z = 50, topn = 10, iterNum = 50;
        COM com = new COM(c, z);
        com.init();
        com.setModel(iterNum);
        int[][] reclist = com.recommend(topn);
        Evaluation.evaluate(name, com.testset, reclist, topn);
    }

    private int[][] recommend(int topn) {
        System.out.println("making recommendation...");
        List<Integer> candlist = new ArrayList<>(getCandEvent());
        double[][] scores = new double[G][candlist.size()];
        int[][] reclist = new int[G][topn];
        double front, back;
        for (int g = 0; g < testset.length; g++) {
            for (int vi = 0; vi < candlist.size(); vi++) {
                int v = candlist.get(vi);
                front = 1;
                for (int u : groups[g]) {
                    back = 0;
                    for (int z = 0; z < Z; z++) {
                        back += model.gz[g][z] * model.zu[z][u] * (model.lambda[u][0] * model.zi[z][v] + (1 - model.lambda[u][0]) * model.ui[u][v]);
                    }
                    front = front * back;
                }
                scores[g][vi] = front;
            }
        }

        for (int g = 0; g < G; g++) {
            int[] events = Util.descSort(candlist, scores[g]);
            for (int v = 0; v < topn; v++) {
                System.arraycopy(events, 0, reclist[g], 0, topn);
            }
        }
        for (int g = 0; g < Input.g_num; g++) {
            for (int i = 0; i < topn; i++) {
                System.out.print(reclist[g][i] + "\t");
            }
            System.out.println();
        }
        Dataset.saveScores(name, scores);
        Dataset.saveResults(name, reclist);
        return reclist;
    }

    private void gibbs() {
        double[] P = new double[Z];
        double[] Pcj = new double[C];
        for (int g = 0; g < trainset.length; g++) {
            for (int v : trainset[g]) {
                paramsIteration(-1, g, v, Zg[g], Cg[g]);
                for (int z = 0; z < Z; z++) {
                    double front = (ngz[g][z] + alpha) / (ngzSum[g] + Z * alpha);
                    double count = groups[g].size() * beta;
                    for (int u : groups[g]) {
                        count = count + nzu[z][u];
                    }
                    front = front * (count / (nzuSum[z] + U * beta));
                    if (Cg[g] == 1) {
                        front = front * ((nzi[z][v] + eta) / (nziSum[z] + I * eta));
                    }
                    P[z] = front;
                }

                if (Cg[g] == 1) {
                    double numerator = groups[g].size() * gamma;
                    double denominator = groups[g].size() * (gamma + gamma_t);
                    for (int u : groups[g]) {
                        numerator = numerator + nuc[u][1];
                        denominator = denominator + nuc[u][1] + nuc[u][0];
                    }
                    Pcj[Cg[g]] = (numerator / denominator) * ((nzi[Zg[g]][v] + eta) / (nziSum[Zg[g]] + I * eta));
                }
                if (Cg[g] == 0) {
                    double numerator = groups[g].size() * gamma;
                    double denominator = groups[g].size() * (gamma + gamma_t);
                    double numerator_2 = groups[g].size() * rho;
                    double denominator_2 = groups[g].size() * I * rho;
                    for (int u : groups[g]) {
                        numerator = numerator + nuc[u][0];
                        denominator = denominator + nuc[u][1] + nuc[u][0];
                        numerator_2 = numerator_2 + nui[u][v];
                        denominator_2 = denominator_2 + nuiSum[u];
                    }
                    Pcj[Cg[g]] = (numerator / denominator) * (numerator_2 / denominator_2);
                }

                Zg[g] = draw(Util.newnorm(P));
                Cg[g] = draw(Util.newnorm(Pcj));
                paramsIteration(1, g, v, Zg[g], Cg[g]);
            }
        }
    }

    private Set<Integer> getCandEvent() {
        Set<Integer> cand = new HashSet<>();
        for (List<Integer> list : testset) {
            cand.addAll(list);
        }
        return cand;
    }

    private double[][] estParameter(int[][] m, int[] sum, double sp) {
        int r = m.length;
        int c = m[0].length;
        double[][] p = new double[r][c];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                p[i][j] = 1.0 * 100000 * (m[i][j] + sp) / (sum[i] + c * sp);
            }
        }
        return p;
    }

    private void setModel(int iterNum) {
        train(iterNum);
        model.gz = estParameter(ngz, ngzSum, alpha);
        model.zu = estParameter(nzu, nzuSum, beta);
        model.ui = estParameter(nui, nuiSum, rho);
        model.zi = estParameter(nzi, nziSum, eta);
        model.lambda = getLambda();
        /*Dataset.saveModelData(model.gz, "gz");
        Dataset.saveModelData(model.zu, "zu");
        Dataset.saveModelData(model.ui, "ui");
        Dataset.saveModelData(model.zi, "zi");
        Dataset.saveModelData(model.lambda, "lambda");*/
    }

    private double[][] getLambda() {
        double[][] lambda = new double[U][1];
        for (int i = 0; i < U; i++) {
            lambda[i][0] = 1.0 * (nuc[i][1] + gamma) / (nuc[i][1] + nuc[i][0] + gamma + gamma_t);
        }
        return lambda;
    }

    private int draw(double[] distribution) {
        double p = Math.random();
        for (int i = 0; i < distribution.length; i++) {
            p = p - distribution[i];
            if (p < 0) {
                return i;
            }
        }
        return distribution.length - 1;
    }

    private void init() {
        System.out.println("init model...");
        Random rand = new Random();
        for (int g = 0; g < trainset.length; g++) {
            Zg[g] = rand.nextInt(Z);
            Cg[g] = rand.nextInt(C);
            for (int v : trainset[g]) {
                paramsIteration(1, g, v, Zg[g], Cg[g]);
            }
        }
    }

    private void train(int num) {
        for (int i = 0; i < num; i++) {
            gibbs();
            System.out.println("No." + i + " training");
        }
    }

    private void paramsIteration(int t, int g, int v, int z, int c) {
        ngz[g][z] += t;
        ngzSum[g] += t;
        nzi[z][v] += t;
        nziSum[z] += t;
        for (int u : groups[g]) {
            nzu[z][u] += t;
            nzuSum[z] += t;
            nui[u][v] += t;
            nuiSum[u] += t;
            nuc[u][c] += t;
        }
    }

    private COM(int c, int z) {
        model = new CModel();
        trainset = Dataset.readTrainOrTestOrGroup(Input.trainfile);
        testset = Dataset.readTrainOrTestOrGroup(Input.testfile);
        groups = Dataset.readTrainOrTestOrGroup(Input.groupfile);
        if (trainset == null || testset == null || groups == null) {
            System.out.println("Dataset is null!");
            return;
        }

        C = c;
        Z = z;
        U = Input.u_num;
        I = Input.v_num;
        G = Input.g_num;

        alpha = 1;
        beta = 0.01;
        eta = 0.01;
        gamma = 0.5;
        gamma_t = 0.5;
        rho = 0.01;

        ngz = new int[G][Z];
        ngzSum = new int[G];
        nzu = new int[Z][U];
        nzuSum = new int[Z];
        nzi = new int[Z][I];
        nziSum = new int[Z];
        nui = new int[U][I];
        nuiSum = new int[U];
        nuc = new int[U][C];

        Cg = new int[G];
        Zg = new int[G];
    }
}
