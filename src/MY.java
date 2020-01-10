import java.util.*;

public class MY {

    static class MModel {
        double[][] lambda;
        double[][] gz;
        double[][] uv;
        double[][] zv;
        double[][] zu;
        double[][] zr;
        double[][] rv;
    }

    private int U, V, G, Z, C, R;
    private double alpha, beta, eta, eplisilon, gamma, gamma_t, rho, omega, delta,x;

    private int[][] mig;
    private int[] migsum;
    private int[][] nzv; // 物品i从主题z中提取的次数，不包括第j个用户-物品对
    private int[] nzvSum;
    private int[][] ngz; // 主题z被分配给g组的次数，不包括第j个用户-物品对
    private int[] ngzSum;
    private int[][] nzu; // 用户u从主题z中抽取的次数，不包括第j个用户-物品对
    private int[] nzuSum;
    private int[][] nuv; // 物品i从用户u中提取的次数，不包括第j个用户-物品对
    private int[] nuvSum;
    private int[][] nuc; // 开关c从用户u中提取的次数，不包括第j个用户-物品对
    private int[][] nzr;
    private int[] nzrSum;
    private int[][] nrv;
    private int[] nrvSum;
    private int[] Cg;
    private int[] Zg;
    private int[] Rg;
    private MModel model;
    private ArrayList<Integer>[] trainset;
    private ArrayList<Integer>[] testset;
    private ArrayList<Integer>[] groups;
    private static String name = "MY";

    public static void main(String[] args) {
        int c = 2, z = 50, r = 50, topn = 10, iterNum = 50;
        MY my = new MY(c, z, r);
        my.init();
        my.setModel(iterNum);
        int[][] reclist = my.recommend(topn);
        Evaluation.evaluate(name, my.testset, reclist, topn);
    }

    public static Set<Integer>[] getRecs(int topn) {
        int c = 2, z = 50, r = 50, iterNum = 50;
        MY my = new MY(c, z, r);
        my.init();
        my.setModel(iterNum);
        int[][] recs = my.recommend(topn);
        Set<Integer>[] recList = new Set[Input.g_num];
        for (int gidx = 0; gidx < Input.g_num; gidx++) {
            Integer[] tmp = new Integer[recs[gidx].length];
            for (int i = 0; i < tmp.length; i++) {
                tmp[i] = recs[gidx][i];
            }
            recList[gidx] = new HashSet<>(Arrays.asList(tmp));
        }
        return recList;
    }

    private int[][] recommend(int topn) {
        System.out.println("making recommendation...");
        List<Integer> candlist = new ArrayList<>(getCandEvent());
        double[][] scores = new double[G][candlist.size()];
        int[][] reclist = new int[G][topn];
        double su, sr, s;
        for (int g = 0; g < testset.length; g++) {
            for (int vi = 0; vi < candlist.size(); vi++) {
                int v = candlist.get(vi);
                s = 1;
                for (int z = 0; z < Z; z++) {
                    su = 0;
                    for (int u : groups[g]) {
                        su += model.gz[g][z] * model.zu[z][u]
                                * (model.lambda[u][0] * model.zv[z][v] + (1 - model.lambda[u][0]) * model.uv[u][v]);
                    }
                    s *= su;
                    sr = 0;
                    for (int r = 0; r < R; r++) {
                        sr += model.zr[z][r] * model.rv[r][v] * x;
                    }
                    s *= sr;
                }
                scores[g][vi] = s * pm(v);
            }
        }

        for (int g = 0; g < G; g++) {
            int[] events = Util.descSort(candlist, scores[g]);
            for (int v = 0; v < topn; v++) {
                System.arraycopy(events, 0, reclist[g], 0, topn);
            }
        }
//        Dataset.saveScores(name, scores);
//        Dataset.saveResults(name, reclist);
        Util.getRes(name);
        return reclist;
    }

    private void gibbs() {
        double[][] P = new double[Z][R];
        double[] Pcj = new double[C];
        for (int g = 0; g < trainset.length; g++) {
            for (int v : trainset[g]) {
                paramsIteration(-1, g, v, Zg[g], Cg[g], Rg[g]);
                for (int z = 0; z < Z; z++) {
                    double front = (ngz[g][z] + alpha) / (ngzSum[g] + Z * alpha);
                    double count = groups[g].size() * beta;
                    for (int u : groups[g]) {
                        count = count + nzu[z][u];
                    }
                    front = front * (count / (nzuSum[z] + U * beta));
                    if (Cg[g] == 1) {
                        front = front * ((nzv[z][v] + eta) / (nzvSum[z] + V * eta));
                    }
                    for (int r = 0; r < R; r++) {
                        P[z][r] = (nzr[z][r] + eplisilon) / (nzrSum[z] + R * eplisilon) * (nrv[r][v] + omega)
                                / (nrvSum[r] + V * omega) * front;
                    }
                }

                if (Cg[g] == 1) {
                    double numerator = groups[g].size() * gamma;
                    double denominator = groups[g].size() * (gamma + gamma_t);
                    for (int u : groups[g]) {
                        numerator = numerator + nuc[u][1];
                        denominator = denominator + nuc[u][1] + nuc[u][0];
                    }
                    Pcj[Cg[g]] = (numerator / denominator) * ((nzv[Zg[g]][v] + eta) / (nzvSum[Zg[g]] + V * eta));
                }
                if (Cg[g] == 0) {
                    double numerator = groups[g].size() * gamma;
                    double denominator = groups[g].size() * (gamma + gamma_t);
                    double numerator_2 = groups[g].size() * rho;
                    double denominator_2 = groups[g].size() * V * rho;
                    for (int u : groups[g]) {
                        numerator = numerator + nuc[u][0];
                        denominator = denominator + nuc[u][1] + nuc[u][0];
                        numerator_2 = numerator_2 + nuv[u][v];
                        denominator_2 = denominator_2 + nuvSum[u];
                    }
                    Pcj[Cg[g]] = (numerator / denominator) * (numerator_2 / denominator_2);
                }

                double[] ravel = Util.newnorm(Util.ravel(P));// 按行来将二维数组转为一维数组
                int index = draw(ravel);
                int[] zr = Util.unravel_index(index, Z, R);
                if (zr == null) {
                    System.out.println("zr is null!");
                    return;
                }
                Zg[g] = zr[0];
                Rg[g] = zr[1];
                Cg[g] = draw(Util.newnorm(Pcj));
                paramsIteration(1, g, v, Zg[g], Cg[g], Rg[g]);
            }
        }
    }

    private void initPM() {
        for (ArrayList<Integer> test : testset) {
            for (int r : test) {
                for (ArrayList<Integer> train : trainset) {
                    for (int c : train) {
                        mig[r][c]++;
                    }
                }
            }
        }
        initPMSum();
    }

    private void initPMSum() {
        for (int c = 0; c < V; c++) {
            for (int r = 0; r < V; r++) {
                migsum[c] += mig[r][c];
            }
        }
    }

    private double pm(int v) {
        int c = 0;
        for (int j = 0; j < Input.v_num; j++) {
            if (mig[v][j] > mig[v][c]) {
                c = j;
            }
        }
        return 1.0 * (mig[v][c] + delta) / (migsum[c] + Input.te_num * delta);
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
                p[i][j] = 1.0 * (m[i][j] + sp) / (sum[i] + c * sp);
            }
        }
        return p;
    }

    private void setModel(int iterNum) {
        initPM();
        train(iterNum);
        model.gz = estParameter(ngz, ngzSum, alpha);
        model.zu = estParameter(nzu, nzuSum, beta);
        model.uv = estParameter(nuv, nuvSum, rho);
        model.zv = estParameter(nzv, nzvSum, eta);
        model.zr = estParameter(nzr, nzrSum, eplisilon);
        model.rv = estParameter(nrv, nrvSum, omega);
        model.lambda = getLambda();
        // Dataset.saveModelData(model.gz, "gz");
        // Dataset.saveModelData(model.zu, "zu");
        // Dataset.saveModelData(model.uv, "uv");
        // Dataset.saveModelData(model.zv, "zv");
        // Dataset.saveModelData(model.zr, "zr");
        // Dataset.saveModelData(model.rv, "rv");
        // Dataset.saveModelData(model.lambda, "lambda");
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
            Rg[g] = rand.nextInt(R);
            for (int v : trainset[g]) {
                paramsIteration(1, g, v, Zg[g], Cg[g], Rg[g]);
            }
        }
    }

    private void train(int num) {
        for (int i = 0; i < num; i++) {
            gibbs();
            System.out.println("No." + i + " training");
        }
    }

    private void paramsIteration(int t, int g, int v, int z, int c, int r) {
        ngz[g][z] += t;
        ngzSum[g] += t;
        nzv[z][v] += t;
        nzvSum[z] += t;
        nzr[z][r] += t;
        nzrSum[z] += t;
        nrv[r][v] += t;
        nrvSum[r] += t;
        for (int u : groups[g]) {
            nzu[z][u] += t;
            nzuSum[z] += t;
            nuv[u][v] += t;
            nuvSum[u] += t;
            nuc[u][c] += t;
        }
    }

    public MY(int c, int z, int r) {
        model = new MModel();
        trainset = Dataset.readTrainOrTestOrGroup(Input.trainfile);
        testset = Dataset.readTrainOrTestOrGroup(Input.testfile);
        groups = Dataset.readTrainOrTestOrGroup(Input.groupfile);
        if (trainset == null || testset == null || groups == null) {
            System.out.println("Dataset is null!");
            return;
        }

        C = c;
        Z = z;
        R = r;
        U = Input.u_num;
        V = Input.v_num;
        G = Input.g_num;

        alpha = 1;
        beta = 0.01;
        rho = 0.01;
        eta = 0.01;
        gamma = 0.5;
        gamma_t = 0.5;
        eplisilon = 0.2;
        omega = 0.01;
        delta = 0.01;
        x = 1000;

        mig = new int[V][V];
        migsum = new int[V];
        ngz = new int[G][Z];
        ngzSum = new int[G];
        nzu = new int[Z][U];
        nzuSum = new int[Z];
        nzr = new int[Z][R];
        nzrSum = new int[Z];
        nrv = new int[R][V];
        nrvSum = new int[R];
        nzv = new int[Z][V];
        nzvSum = new int[Z];
        nuv = new int[U][V];
        nuvSum = new int[U];
        nuc = new int[U][C];

        Cg = new int[G];
        Zg = new int[G];
        Rg = new int[G];
    }
}
