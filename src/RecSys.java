import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RecSys {
    private int U, V, Z, R, G;
    private double alpha = 1, beta = 0.01, eta = 0.2, gamma = 0.01, omega = 0.01;

    private int[] Zg;
    private int[] Rg;

    private int[][] nzr;
    private int[][] ngz;
    private int[][] nzu;
    private int[][] nzv;
    private int[][] nrv;

    private int[] ngzsum;
    private int[] nzrsum;
    private int[] nzusum;
    private int[] nzvsum;
    private int[] nrvsum;

    private double[][] mu;
    private double[][] sigma;

    private int[] eventLoca;
    private int[] eventTime;
    private double[][] locations;
    private ArrayList<Integer>[] groups;
    private ArrayList<Integer>[] trainset;
    private ArrayList<Integer>[] testset;

    public RecSys(int Z, int R) {
        this.Z = Z;
        this.R = R;
        U = Dataset.countNum(Input.umapfile);
        V = Dataset.countNum(Input.emapfile);
        G = Dataset.countNum(Input.gmapfile);
        locations = Dataset.readLocation(Input.locationfile);
        eventLoca = Dataset.readEventLocation(Input.eventfile);
        eventTime = Dataset.readEventTime(Input.eventfile);
        trainset = Dataset.readTrainOrTestOrGroup(Input.trainfile);
        testset = Dataset.readTrainOrTestOrGroup(Input.testfile);
        groups = Dataset.readTrainOrTestOrGroup(Input.groupfile);
        if (groups == null) {
            System.out.println("TrainSet is null!");
            return;
        }

        Zg = new int[G];
        Rg = new int[G];
        ngz = new int[G][Z];
        nzu = new int[Z][U];
        nzv = new int[Z][V];
        nrv = new int[R][V];
        nzr = new int[Z][R];

        ngzsum = new int[G];
        nzrsum = new int[Z];
        nzusum = new int[Z];
        nzvsum = new int[Z];
        nrvsum = new int[R];
        mu = new double[R][2];
        sigma = new double[R][2];
    }

    private void initialize() {
        System.out.println("initializing model...");
        Random rand = new Random();
        for (int g = 0; g < trainset.length; g++) {
            for (int e : trainset[g]) {
                Zg[g] = rand.nextInt(Z);
                Rg[g] = rand.nextInt(R);
                paramsIteration(1, g, e, Zg[g], Rg[g]);
            }
        }

        for (int r = 0; r < R; r++) {
            mu[r][0] = 39.9 + rand.nextFloat();
            mu[r][1] = 116.3 + rand.nextFloat();
            sigma[r][0] = rand.nextFloat() * 10;
            sigma[r][1] = rand.nextFloat() * 10;
        }
    }

    private void paramsIteration(int t, int g, int e, int z, int r) {
        ngz[g][z] += t;
        ngzsum[g] += t;
        nzv[z][e] += t;
        nzvsum[z] += t;
        nrv[r][e] += t;
        nrvsum[r] += t;
        nzr[z][r] += t;
        nzrsum[z] += t;
        for (int u : groups[g]) {
            nzu[z][u] += t;
            nzusum[z] += t;
        }
    }

    public int draw(double[] a) {
        double r = Math.random();
        for (int i = 0; i < a.length; i++) {
            r = r - a[i];
            if (r < 0)
                return i;
        }
        return a.length - 1;
    }

    private void gibbs() {
        int z, r;
        double[][] P = new double[Z][R];
        for (int g = 0; g < trainset.length; g++) {
            for (int e : trainset[g]) {
                paramsIteration(-1, g, e, Zg[g], Rg[g]);
                for (z = 0; z < Z; z++) {
                    double zp = (ngz[g][z] + alpha) / (ngzsum[g] + Z * alpha) * (nzv[z][e] + gamma)
                            / (nzvsum[z] + V * gamma);
                    for (int u : groups[g]) {
                        zp *= (nzu[z][u] + beta) / (nzusum[z] + U * beta);
                    }
                    for (r = 0; r < R; r++) {
                        P[z][r] = (nzr[z][r] + eta) / (nzrsum[z] + R * eta) * (nrv[r][e] + omega)
                                / (nrvsum[r] + V * omega) * zp * pdf(e, r);
                    }
                }

                double[] Pravel = Util.newnorm(Util.ravel(P));// 按行来将二维数组转为一维数组
                // Util.print(Pravel);
                int index = draw(Pravel);
                // System.out.println(index);
                int[] zr = Util.unravel_index(index, Z, R);
                if (zr == null) {
                    System.out.println("zr is null!");
                    return;
                }
                Zg[g] = zr[0];
                Rg[g] = zr[1];
                paramsIteration(1, g, e, Zg[g], Rg[g]);
            }
        }

        for (r = 0; r < R; r++) {
            updateGaussian(r);
        }
    }

    public void train(int iterNum) {
        // iterNum is the number of iterations
        for (int it = 0; it < iterNum; it++) {
            gibbs();
            System.out.println("iteration " + (it + 1));
        }
    }

    private double pdf(int e, int r) {
        int l = eventLoca[e];
        double x = locations[l][0] - mu[r][0];
        double y = locations[l][1] - mu[r][1];
        double temp = Math.exp(-0.5 * ((x * x) / (sigma[r][0] * sigma[r][0]) + (y * y) / (sigma[r][1] * sigma[r][1])));
        return temp / (2 * 3.142 * sigma[r][0] * sigma[r][1]);
    }

    private void updateGaussian(int r) {
        List<Integer> lr = new ArrayList<>();
        for (int g = 0; g < trainset.length; g++) {
            if (Rg[g] == r)
                lr.addAll(trainset[g]);
        }
        if (lr.size() <= 1)
            return;
        for (int e : lr) {
            int l = eventLoca[e];
            mu[r][0] += locations[l][0];
            mu[r][1] += locations[l][1];
        }
        mu[r][0] /= lr.size();
        mu[r][1] /= lr.size();
        for (int e : lr) {
            int l = eventLoca[e];
            sigma[r][0] += (locations[l][0] - mu[r][0]) * (locations[l][0] - mu[r][0]);
            sigma[r][1] += (locations[l][1] - mu[r][1]) * (locations[l][1] - mu[r][1]);
        }
        sigma[r][0] /= lr.size() - 1;
        sigma[r][1] /= lr.size() - 1;
    }

    private double[][] estParameter(int[][] m, int[] sum, double sp) {
        int r = m.length;
        int c = m[0].length;
        double[][] p = new double[r][c];
        for (int i = 0; i < r; i++)
            for (int j = 0; j < c; j++)
                p[i][j] = (m[i][j] + sp) / (sum[i] + c * sp);
        return p;
    }

    public Model getModel() {
        Model model = new Model();
        model.phiGZ = estParameter(ngz, ngzsum, alpha);
        model.phiZR = estParameter(nzr, nzrsum, eta);
        model.phiZV = estParameter(nzv, nzvsum, gamma);
        model.phiRV = estParameter(nrv, nrvsum, omega);
        model.phiZU = estParameter(nzu, nzusum, beta);
        // inference(model);
        saveModel(model);
        return model;
    }

    private void saveModel(Model model) {
        Dataset.saveModelData(model.phiGZ, "phiGZ");
        Dataset.saveModelData(model.phiZR, "phiZR");
        Dataset.saveModelData(model.phiZV, "phiZV");
        Dataset.saveModelData(model.phiRV, "phiRV");
        Dataset.saveModelData(model.phiZU, "phiZU");
    }

    private Model readModel() {
        Model model = new Model();
        model.phiGZ = Dataset.readModelData("phiGZ");
        model.phiZR = Dataset.readModelData("phiZR");
        model.phiZV = Dataset.readModelData("phiZV");
        model.phiRV = Dataset.readModelData("phiRV");
        model.phiZU = Dataset.readModelData("phiZU");
        return model;
    }

    /*
     * private void inference(Model model) { // Map<Integer,double[]> theta = new
     * HashMap<Integer,double[]>(); Random rand = new Random(); int gnum = G;
     * int[][] ngz = new int[gnum][Z]; int[] ngzsum = new int[gnum]; int[] Z = new
     * int[gnum]; int z; for (int gid : testset.keySet()) { z =
     * rand.nextInt(this.Z); Z[gid] = z; ngz[gid][z]++; ngzsum[gid]++; } //
     * Util.print(ngz); double[] p = new double[this.Z]; for (int iter = 0; iter <
     * iterNum; iter++) { for (int gid : testset.keySet()) { z = Z[gid];
     * ngz[gid][z]--; ngzsum[gid]--; for (int k = 0; k < this.Z; k++) { p[k] =
     * (ngz[gid][k] + alpha); for (int u : groups.get(gid)) { p[k] *=
     * model.phiZU[k][u]; } } Util.norm(p); z = draw(p); Z[gid] = z; ngz[gid][z]++;
     * ngzsum[gid]++; } } // Util.print(ngz); model.phiGZ = estParameter(ngz,
     * ngzsum, alpha); }
     */

    private Set<Integer> getCandEvent() {
        Set<Integer> cand = new HashSet<>();
        for (List<Integer> list : testset) {
            cand.addAll(list);
        }
        return cand;
    }

    private int[][] recommend(Model model, int topn) {
        System.out.println("making recommendation...");
        List<Integer> candlist = new ArrayList<>(getCandEvent());
        double[][] score = new double[G][candlist.size()];
        int[][] reclist = new int[G][topn];
        double s = 0, sr, su;
        for (int g = 0; g < testset.length; g++) {
            for (int v = 0; v < candlist.size(); v++) {
                for (int z = 0; z < Z; z++) {
                    s = model.phiGZ[g][z] * model.phiZV[z][candlist.get(v)];
                    su = 1;
                    for (int u : groups[g]) {
                        su *= model.phiZU[z][u];
                    }
                    // System.out.println("su: " + su);
                    s *= Math.pow(su, 1.0 / groups[g].size());
                    sr = 0;
                    for (int r = 0; r < R; r++) {
                        sr += model.phiZR[z][r] * model.phiRV[r][candlist.get(v)] * pdf(candlist.get(v), r);
                    }
                    // System.out.println("sr: " + sr);
                    s *= sr;
                }
                score[g][v] += s;
                // System.out.println(score[g][v]);
            }
        }

        for (int g = 0; g < G; g++) {
            int[] events = Util.descSort(score[g]);
            for (int v = 0; v < topn; v++) {
                System.arraycopy(events, 0, reclist[g], 0, topn);
            }
        }
        Dataset.saveResults(reclist);
        return reclist;
    }

    public static void main(String[] args) {
        // long startTime = System.currentTimeMillis();
        int Z = 50, R = 50, iterNum = 20, topn = 10; // K is number of topics, R is number of regions
        RecSys recSys = new RecSys(Z, R);
        /*recSys.initialize();
        recSys.train(iterNum);
        Model model = recSys.getModel();*/
        Model model = recSys.readModel();
        int[][] reclist = recSys.recommend(model, topn);
        Evaluation.evaluate(recSys.testset, reclist, "Recall", topn); // evaluate the precision and recall of
                                                                      // recommendations

        // long endTime = System.currentTimeMillis();
        // System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
        // for(int i=0;i<50;i++)
        // Util.print(list.get(i));
        // Evaluation.evaluate(list, recSys.testset, "Recall", 5);
        // list = IO.readRecList("dataset/douban/Reclist_2.txt");
    }
}
