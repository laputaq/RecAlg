import java.io.*;
import java.util.*;

public class Dataset {

    private static int countNum(String file) {
        int num = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            while (reader.readLine() != null) {
                num++;
            }
            return num;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    class Event {
        Date sTime;
        int num;
    }

    static int[] readEventLocation(String fileName) {
        List<Event> a = new ArrayList<>();
        a.sort(Comparator.comparing(o -> o.sTime));
        int N = countNum(fileName);
        int[] eventLoc = new int[N];
        File file = new File(fileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.split(",");
                int eid = Integer.parseInt(tmp[0]);
                int lid = Integer.parseInt(tmp[1]);
                eventLoc[eid] = lid;
            }
            return eventLoc;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static int[] readEventTime(String fileName) {
        int N = countNum(fileName);
        int[] eventLoc = new int[N];
        File file = new File(fileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.split(",");
                int eid = Integer.parseInt(tmp[0]);
                int time = Integer.parseInt(tmp[2]);
                eventLoc[eid] = time;
            }
            return eventLoc;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static double[][] readLocation(String fileName) {
        Map<Integer, double[]> locMap = new HashMap<>();
        File file = new File(fileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.split(",");
                double[] sample = new double[tmp.length - 1];
                for (int i = 1; i < tmp.length; i++) {
                    sample[i - 1] = Double.parseDouble(tmp[i]);
                }
                locMap.put(Integer.parseInt(tmp[0]), sample);
            }
            double[][] locations = new double[locMap.size()][];
            for (int lid : locMap.keySet()) {
                locations[lid] = locMap.get(lid);
            }
            return locations;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static ArrayList<Integer>[] readTrainOrTestOrGroup(String fileName) {
        ArrayList<Integer>[] groupEvents = new ArrayList[Input.g_num];
        File file = new File(fileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] temp = line.split(",");
                int gid = Integer.parseInt(temp[0]);
                int id = Integer.parseInt(temp[1]);
                if (groupEvents[gid] == null) {
                    groupEvents[gid] = new ArrayList<>();
                }
                groupEvents[gid].add(id);
            }
            return groupEvents;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double[][] readModel(String fileName, String sep) {
        List<double[]> data = new ArrayList<double[]>();
        File file = new File(fileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] temp = line.split(sep);
                double[] sample = new double[temp.length];
                for (int i = 0; i < temp.length; i++) {
                    sample[i] = Double.parseDouble(temp[i]);
                }
                data.add(sample);
            }
            reader.close();
            return data.toArray(new double[][]{});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static int[][] readLocTime() {
        int[][] res = new int[Input.v_num][2];
        File inputFile = new File("dataset/meetup/events.csv");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(inputFile));

            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.split(",");
                int e_id = Integer.parseInt(tmp[0]);
                int l_id = Integer.parseInt(tmp[1]);
                int time = Integer.parseInt(tmp[2]);
                res[e_id] = new int[]{l_id, time};
            }
            return res;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    static void saveModelData(double[][] arrs, String fileName) {
        File file = new File("dataset/meetup/model/" + fileName);
        BufferedWriter writer = null;
        StringBuilder sb;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            for (double[] arr : arrs) {
                sb = new StringBuilder();
                for (int i = 0; i < arr.length - 1; i++) {
                    sb.append(arr[i]).append("\t");
                }
                sb.append(arr[arr.length - 1]).append("\n");
                writer.write(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    static double[][] readModelData(String fileName) {
        File file = new File("dataset/meetup/model/" + fileName);
        List<double[]> res = new ArrayList<>();
        BufferedReader reader = null;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.split("\t");
                double[] items = new double[tmp.length];
                for (int i = 0; i < tmp.length; i++) {
                    items[i] = Integer.parseInt(tmp[i]);
                }
                res.add(items);
            }
            return res.toArray(new double[][]{});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    static void saveResults(String name, int[][] arrs) {
        File file = new File("./res/" + name + "_reclist.txt");
        BufferedWriter writer = null;
        StringBuilder sb;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            for (int[] arr : arrs) {
                sb = new StringBuilder();
                for (int i = 0; i < arr.length - 1; i++) {
                    sb.append(arr[i]).append("\t");
                }
                sb.append(arr[arr.length - 1]).append("\n");
                writer.write(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    static void saveScores(String name, double[][] scores) {
        File file = new File("./res/" + name + "_scores.txt");
        StringBuilder sb;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (double[] score : scores) {
                sb = new StringBuilder();
                for (int i = 0; i < score.length - 1; i++) {
                    sb.append(score[i]).append("\t");
                }
                sb.append(score[score.length - 1]).append("\n");
                writer.write(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void saveEvaluation(String name, int topn, int hit, int sum, double p, double r, double f1) {
        File file = new File("./res/" + name + "_evaluation.txt");
        StringBuilder sb = new StringBuilder();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            sb.append("hit=").append(hit).append("\n")
                    .append("sum=").append(sum).append("\n")
                    .append(String.format("Precision=%.4f%%\n", p))
                    .append(String.format("Recall=%.4f%%\n", r))
                    .append(String.format("F1-Measure=%.4f%%\n", f1));
            writer.write(sb.toString());
            System.out.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static Calendar calendar = Calendar.getInstance();

    private static int getWeek(int time) {
        calendar.setTimeInMillis(1000L * time);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static void main(String[] args) throws IOException {
        setDatasetNum();
    }

    private static void setDatasetNum() {
        HashSet<Integer> trSet = new HashSet<>();
        HashSet<Integer> teSet = new HashSet<>();
        try (BufferedReader train = new BufferedReader(new FileReader(Input.trainfile)); BufferedReader test = new BufferedReader(new FileReader(Input.testfile))) {
            String line;
            train.readLine();
            while ((line = train.readLine()) != null) {
                String[] tmp = line.split(",");
                trSet.add(Integer.parseInt(tmp[1]));
            }
            test.readLine();
            while ((line = test.readLine()) != null) {
                String[] tmp = line.split(",");
                teSet.add(Integer.parseInt(tmp[1]));
            }
            System.out.println(trSet.size());
            System.out.println(teSet.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sameEvents() throws IOException {
        File file = new File("dataset/meetup/events.csv");
        File file2 = new File("dataset/meetup/events2.csv");
        File train = new File("dataset/meetup/train.csv");
        File test = new File("dataset/meetup/test.csv");
        File train2 = new File("dataset/meetup/train2.csv");
        File test2 = new File("dataset/meetup/test2.csv");
        ArrayList<Integer>[] locEid = new ArrayList[Input.l_num];
        HashMap<Integer, Integer> transfer = new HashMap<>();
        int[] eidTime = new int[Input.v_num];
        String line, content;
        try (BufferedReader reader = new BufferedReader(new FileReader(file)); BufferedReader reader1 = new BufferedReader(new FileReader(file)); BufferedWriter writer = new BufferedWriter(new FileWriter(file2)); BufferedReader trainr = new BufferedReader(new FileReader(train)); BufferedReader testr = new BufferedReader(new FileReader(test)); BufferedWriter trainw = new BufferedWriter(new FileWriter(train2)); BufferedWriter testw = new BufferedWriter(new FileWriter(test2))) {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.split(",");
                int eid = Integer.parseInt(tmp[0]);
                int lid = Integer.parseInt(tmp[1]);
                int time = Integer.parseInt(tmp[2]);
                if (locEid[lid] == null) {
                    locEid[lid] = new ArrayList<>();
                }
                locEid[lid].add(eid);
                eidTime[eid] = time;
            }
            int ind = 0;
            for (ArrayList<Integer> events : locEid) {
                for (int i = 0; i < events.size(); i++) {
                    if (transfer.containsKey(events.get(i))) continue;
                    transfer.put(events.get(i), ind);
                    for (int j = i + 1; j < events.size(); j++) {
                        if (transfer.containsKey(events.get(j))) continue;
                        if (getWeek(eidTime[events.get(i)]) == getWeek(eidTime[events.get(j)])) { //same events
                            transfer.put(events.get(j), ind);
                        }
                    }
                    ind++;
                }
            }
            for (int key : transfer.keySet()) {
                System.out.println("key: " + key + ", val: " + transfer.get(key));
            }
            System.out.println("ind: " + ind);
            ArrayList<String>[] event = new ArrayList[ind];
            line = reader1.readLine();
            writer.write(line + "\n");
            while ((line = reader1.readLine()) != null) {
                String[] tmp = line.split(",");
                int eid = Integer.parseInt(tmp[0]);
                int lid = Integer.parseInt(tmp[1]);
                int time = Integer.parseInt(tmp[2]);
                eid = transfer.get(eid);
                content = eid + "," + lid + "," + time + "\n";
                if (event[eid] == null) {
                    event[eid] = new ArrayList<>();
                }
                event[eid].add(content);
            }
            for (ArrayList<String> items : event) {
                for (String item : items) {
                    writer.write(item);
                }
            }
            ArrayList<Integer>[] tr_t = new ArrayList[Input.g_num];
            line = trainr.readLine();
            trainw.write(line + "\n");
            while ((line = trainr.readLine()) != null) {
                String[] tmp = line.split(",");
                int gid = Integer.parseInt(tmp[0]);
                int eid = Integer.parseInt(tmp[1]);
                eid = transfer.get(eid);
                if (tr_t[gid] == null) {
                    tr_t[gid] = new ArrayList<>();
                }
                tr_t[gid].add(eid);
            }
            for (int g = 0; g < tr_t.length; g++) {
                Collections.sort(tr_t[g]);
                for (int e : tr_t[g]) {
                    trainw.write(g + "," + e + "\n");
                }
            }
            ArrayList<Integer>[] te_t = new ArrayList[Input.g_num];
            line = testr.readLine();
            testw.write(line + "\n");
            while ((line = testr.readLine()) != null) {
                String[] tmp = line.split(",");
                int gid = Integer.parseInt(tmp[0]);
                int eid = Integer.parseInt(tmp[1]);
                eid = transfer.get(eid);
                if (te_t[gid] == null) {
                    te_t[gid] = new ArrayList<>();
                }
                te_t[gid].add(eid);
            }
            for (int g = 0; g < te_t.length; g++) {
                Collections.sort(te_t[g]);
                for (int e : te_t[g]) {
                    testw.write(g + "," + e + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
