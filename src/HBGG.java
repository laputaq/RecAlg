import java.util.*;

public class HBGG {

    static class HModel {
        double[][] phiZU;
        double[][] phiZV;
        double[][] phiRV;
        double[][] phiZR;
        double[][] phiGZ;
    }

    private int U, V, G, Z, R;
    private double alpha = 1, beta = 0.01, eta = 0.2, gamma = 0.01, omega = 0.01;

    private int[] Zg;
    private int[] Rg;
    private int[][] ngz;
    private int[][] nzr;
    private int[][] nrv;
    private int[][] nzu;
    private int[][] nzv;

    private int[] ngzsum;
    private int[] nzrsum;
    private int[] nrvsum;
    private int[] nzusum;
    private int[] nzvsum;

    private ArrayList<Integer>[] trainset;
    private ArrayList<Integer>[] testset;
    private ArrayList<Integer>[] groups;
    private static String name = "HBGG";

    private HBGG(int z, int r) {
        Z = z;
        R = r;
        U = Input.u_num;
        V = Input.v_num;
        G = Input.g_num;
        trainset = Dataset.readTrainOrTestOrGroup(Input.trainfile);
        testset = Dataset.readTrainOrTestOrGroup(Input.testfile);
        groups = Dataset.readTrainOrTestOrGroup(Input.groupfile);
        if (trainset == null || testset == null || groups == null) {
            System.out.println("Dataset is null!");
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
    }

    public static void main(String[] args) {
        int Z = 50, R = 50, topn = 10, iterNum = 50;
        HBGG HBGG = new HBGG(Z, R);
        HBGG.init();
        HModel model = HBGG.getModel(iterNum);
        //HModel model = recAlg.readModel();
        int[][] reclist = HBGG.recommend(model, topn);
        Evaluation.evaluate(name, HBGG.testset, reclist, topn);
    }

    private int[][] recommend(HModel model, int topn) {
        System.out.println("making recommendation...");
        List<Integer> candlist = new ArrayList<>(getCandEvent());
        double[][] scores = new double[G][candlist.size()];
        int[][] reclist = new int[G][topn];
        double s = 0, sr, su;
        for (int g = 0; g < testset.length; g++) {
            for (int vi = 0; vi < candlist.size(); vi++) { //v is index of event
                int v = candlist.get(vi);
                for (int z = 0; z < Z; z++) {
                    s = model.phiGZ[g][z] * model.phiZV[z][v];
                    su = 1;
                    for (int u : groups[g]) {
                        su *= Math.pow(model.phiZU[z][u], 1.0 / groups[g].size());
                    }
                    s *= su;
                    sr = 0;
                    for (int r = 0; r < R; r++) {
                        sr += model.phiZR[z][r] * model.phiRV[r][v];
                    }
                    s *= sr;
                }
                scores[g][vi] = s;
            }
        }

        for (int g = 0; g < G; g++) {
            int[] events = Util.descSort(candlist, scores[g]);
            for (int v = 0; v < topn; v++) {
                System.arraycopy(events, 0, reclist[g], 0, topn);
            }
        }
        Dataset.saveScores(name, scores);
        Dataset.saveResults(name, reclist);
        return reclist;
    }

    private void init() {
        System.out.println("init model...");
        Random rand = new Random();
        for (int g = 0; g < trainset.length; g++) {
            Zg[g] = rand.nextInt(Z);
            Rg[g] = rand.nextInt(R);
            for (int v : trainset[g]) {
                paramsIteration(1, g, v, Zg[g], Rg[g]);
            }
        }
    }

    private void paramsIteration(int t, int g, int v, int z, int r) {
        ngz[g][z] += t;
        ngzsum[g] += t;
        nzv[z][v] += t;
        nzvsum[z] += t;
        nrv[r][v] += t;
        nrvsum[r] += t;
        nzr[z][r] += t;
        nzrsum[z] += t;
        for (int u : groups[g]) {
            nzu[z][u] += t;
            nzusum[z] += t;
        }
    }

    private int draw(double[] a) {
        double r = Math.random();
        for (int i = 0; i < a.length; i++) {
            r = r - a[i];
            if (r < 0)
                return i;
        }
        return a.length - 1;
    }

    private void gibbs() {
        double[][] P = new double[Z][R];
        for (int g = 0; g < trainset.length; g++) {
            for (int v : trainset[g]) {
                paramsIteration(-1, g, v, Zg[g], Rg[g]);
                for (int z = 0; z < Z; z++) {
                    double zp = (ngz[g][z] + alpha) / (ngzsum[g] + Z * alpha)
                            * (nzv[z][v] + gamma) / (nzvsum[z] + V * gamma);
                    double count = groups[g].size() * beta;
                    for (int u : groups[g]) {
                        count = count + nzu[z][u];
                    }
                    zp *= (count / (nzusum[z] + U * beta));
                    for (int r = 0; r < R; r++) {
                        P[z][r] = (nzr[z][r] + eta) / (nzrsum[z] + R * eta)
                                * (nrv[r][v] + omega) / (nrvsum[r] + V * omega)
                                * zp;
                    }
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
                paramsIteration(1, g, v, Zg[g], Rg[g]);
            }
        }
    }

    private void train(int iterNum) {
        for (int it = 0; it < iterNum; it++) {
            gibbs();
            System.out.println("iteration " + (it + 1));
        }
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

    private HModel getModel(int iterNum) {
        train(iterNum);
        HModel model = new HModel();
        model.phiGZ = estParameter(ngz, ngzsum, alpha);
        model.phiZR = estParameter(nzr, nzrsum, eta);
        model.phiZV = estParameter(nzv, nzvsum, gamma);
        model.phiRV = estParameter(nrv, nrvsum, omega);
        model.phiZU = estParameter(nzu, nzusum, beta);

        /*Dataset.saveModelData(model.phiGZ, "phiGZ");
        Dataset.saveModelData(model.phiZR, "phiZR");
        Dataset.saveModelData(model.phiZV, "phiZV");
        Dataset.saveModelData(model.phiRV, "phiRV");
        Dataset.saveModelData(model.phiZU, "phiZU");*/
        return model;
    }

    /*private Model readModel() {
        HModel model = new HModel();
        model.phiGZ = Dataset.readModelData("phiGZ");
        model.phiZR = Dataset.readModelData("phiZR");
        model.phiZV = Dataset.readModelData("phiZV");
        model.phiRV = Dataset.readModelData("phiRV");
        model.phiZU = Dataset.readModelData("phiZU");
        return model;
    }*/

    private Set<Integer> getCandEvent() {
        Set<Integer> cand = new HashSet<>();
        for (List<Integer> list : testset) {
            cand.addAll(list);
        }
        return cand;
    }

}
