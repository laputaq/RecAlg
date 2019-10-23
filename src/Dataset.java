import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Dataset {
    public static int U;
    // public static int T;
    public static int W;
    public static int E;
    public static int H;

    static int countNum(String file) {
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

    public static double[][] readRatingMatrix(String fileName, int row, int col) {

        double[][] matrix = new double[row][col];
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = null;

            while ((line = reader.readLine()) != null) {
                String[] temp = line.split(",");
                int gid = Integer.parseInt(temp[0]);
                int eid = Integer.parseInt(temp[1]);
                matrix[gid][eid] = 1;

            }
            reader.close();
            return matrix;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public static int getEventNum(String fileName) {
        int count = 0;
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            while (reader.readLine() != null) {
                count++;
            }
            reader.close();
            return count;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int[][] loadCorpus(String fileName) {
        Map<Integer, List<Integer>> data = new HashMap<Integer, List<Integer>>();
        Set<Integer> eset = new HashSet<Integer>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = null;

            while ((line = reader.readLine()) != null) {
                String[] temp = line.split(",");
                int gid = Integer.parseInt(temp[0]);
                int eid = Integer.parseInt(temp[1]);
                eset.add(eid);
                if (data.containsKey(gid))
                    data.get(gid).add(eid);
                else {
                    List<Integer> list = new ArrayList<Integer>();
                    list.add(eid);
                    data.put(gid, list);
                }

            }
            reader.close();
            int[][] corpus = new int[data.size()][];
            for (int gid : data.keySet()) {
                corpus[gid] = new int[data.get(gid).size()];
                for (int i = 0; i < corpus[gid].length; i++)
                    corpus[gid][i] = data.get(gid).get(i);
            }
            W = eset.size();
            return corpus;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    static int[][] readTrain(String fileName) {
        List<int[]> data = new ArrayList<>();
        File file = new File(fileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] temp = line.split(",");
                int[] sample = new int[2];
                for (int i = 0; i < temp.length; i++) {
                    sample[i] = Integer.parseInt(temp[i]);
                }
                data.add(sample);
            }
            return data.toArray(new int[][] {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int[] readOrganizer(String fileName) {
        List<Integer> org = new ArrayList<Integer>();
        File file = new File(fileName);
        BufferedReader reader = null;
        int[] value;
        Set<Integer> oset = new HashSet<Integer>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                int oid = Integer.parseInt(line);
                org.add(oid);
                oset.add(oid);
            }
            reader.close();
            value = new int[org.size()];
            for (int i = 0; i < org.size(); i++)
                value[i] = org.get(i);
            H = oset.size();
            return value;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    static int[] readEventLocation(String fileName) {
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

    public static int[][] readCorpus(String fileName, String sep) {
        List<int[]> data = new ArrayList<int[]>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] temp = line.split(sep);
                int[] sample = new int[temp.length];
                for (int i = 0; i < temp.length; i++) {
                    sample[i] = Integer.parseInt(temp[i]);
                    if (count < sample[i])
                        count = sample[i];
                }
                data.add(sample);
            }
            reader.close();
            W = count + 1;
            return data.toArray(new int[][] {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public static List<Integer> readCand(String fileName) {
        List<Integer> list = new ArrayList<Integer>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                list.add(Integer.parseInt(line));

            }
            reader.close();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    static ArrayList<Integer>[] readTrainOrTestOrGroup(String fileName) {
        int group_num = 1134;
        ArrayList<Integer>[] groupEvents = new ArrayList[group_num];
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

    public static int getUserNum(int[][] groups) {
        Set<Integer> uset = new HashSet<Integer>();
        for (int[] g : groups)
            for (int u : g)
                uset.add(u);
        return uset.size();
    }

    public static double[][] readModel(String fileName, String sep) {
        List<double[]> data = new ArrayList<double[]>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
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
            return data.toArray(new double[][] {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public static void getChicagoData() {
        File inputFile = new File("dataset\\meetup\\locations.csv");
        File outputFile = new File("dataset\\meetup\\locations_chicago.csv");
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String line = null;
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write("location_id,longitude,latitude\n");
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.split(",");
                if (tmp.length <= 3 || !"Chicago".equals(tmp[3]))
                    continue;
                String s = tmp[0] + "," + tmp[2] + "," + tmp[1] + "\n";
                writer.write(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public static void setChicagoData() {
        File inputFile = new File("dataset\\meetup\\locations_chicago.csv");
        File outputFile = new File("dataset\\meetup\\locations.csv");
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String line = null;
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            writer = new BufferedWriter(new FileWriter(outputFile));
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.split(",");
                if ("0.0".equals(tmp[1]) || "0.0".equals(tmp[2]))
                    continue;
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public static void setData() {
        File file = new File("dataset\\meetup\\locations.csv");
        File inputFile = new File("dataset\\meetup\\events.csv");
        File outputFile = new File("dataset\\meetup\\events_chicago.csv");
        BufferedReader reader = null;
        BufferedWriter writer = null;
        Set<String> lids = new HashSet<>();
        String line = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.split(",");
                lids.add(tmp[0]);
            }

            reader = new BufferedReader(new FileReader(inputFile));
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(reader.readLine() + "\n");
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.split(",");
                if (lids.contains(tmp[2]))
                    writer.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    static class Dump {
        String gid;
        int count;

        Dump(String gid) {
            this.gid = gid;
        }
    }

    public static void cleanData() {
        File inputFile = new File("dataset\\meetup\\events_chicago.csv");
        File outputFile = new File("dataset\\meetup\\events.csv");
        Map<String, Dump> map = new HashMap<>();
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            writer = new BufferedWriter(new FileWriter(outputFile));

            String line = reader.readLine() + "\n";
            writer.write(line);
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.split(",");
                Dump dump = map.getOrDefault(tmp[3], new Dump(tmp[3]));
                dump.count++;
                map.put(tmp[3], dump);
            }
            Dump[] dumps = new Dump[map.size()];
            map.values().toArray(dumps);
            Set<String> remove = new HashSet<>();
            for (Dump item : dumps) {
                if (item.count < 5)
                    remove.add(item.gid);
            }

            System.out.println(remove.size());

            reader = new BufferedReader(new FileReader(inputFile));
            writer = new BufferedWriter(new FileWriter(outputFile));

            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.split(",");
                if (remove.contains(tmp[3]))
                    continue;
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public static void serialData() {
        File inputFile = new File("dataset\\meetup\\events.csv");
        File outputFile = new File("dataset\\meetup\\events_ind.csv");
        File eventFile = new File("dataset\\meetup\\event.csv");
        File gmapFile = new File("dataset\\meetup\\g_map.csv");
        File emapFile = new File("dataset\\meetup\\e_map.csv");
        File lmapFile = new File("dataset\\meetup\\l_map.csv");
        BufferedReader reader = null;
        BufferedWriter writer = null;
        BufferedWriter ewriter = null;
        BufferedWriter writerg = null;
        BufferedWriter writere = null;
        BufferedWriter writerl = null;

        Map<String, Integer> g_map = new HashMap<>();
        Map<String, Integer> e_map = new HashMap<>();
        Map<String, Integer> l_map = new HashMap<>();
        int g_ind = 0, e_ind = 0, l_ind = 0;

        try {
            reader = new BufferedReader(new FileReader(inputFile));
            writer = new BufferedWriter(new FileWriter(outputFile));
            ewriter = new BufferedWriter(new FileWriter(eventFile));
            writerg = new BufferedWriter(new FileWriter(gmapFile));
            writere = new BufferedWriter(new FileWriter(emapFile));
            writerl = new BufferedWriter(new FileWriter(lmapFile));

            String line = reader.readLine() + "\n";
            writer.write(line);
            writerg.write("g_id,group_id\n");
            writere.write("e_id,event_id\n");
            writerl.write("l_id,location_id\n");
            ewriter.write("g_id,e_id\n");
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.split(",");
                if (!g_map.containsKey(tmp[3])) {
                    g_map.put(tmp[3], g_ind++);
                    writerg.write(g_map.get(tmp[3]) + "," + tmp[3] + "\n");
                }
                if (!e_map.containsKey(tmp[0])) {
                    e_map.put(tmp[0], e_ind++);
                    writere.write(e_map.get(tmp[0]) + "," + tmp[0] + "\n");
                }
                if (!l_map.containsKey(tmp[2])) {
                    l_map.put(tmp[2], l_ind++);
                    writerl.write(l_map.get(tmp[2]) + "," + tmp[2] + "\n");
                }
                String event = e_map.get(tmp[0]) + "," + l_map.get(tmp[2]) + "," + tmp[1] + "\n";
                writer.write(event);
                String pair = g_map.get(tmp[3]) + "," + e_map.get(tmp[0]) + "\n";
                ewriter.write(pair);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e1) {
                }
            }
            if (ewriter != null) {
                try {
                    ewriter.close();
                } catch (IOException e1) {
                }
            }
            if (writerl != null) {
                try {
                    writerl.close();
                } catch (IOException e1) {
                }
            }
            if (writerg != null) {
                try {
                    writerg.close();
                } catch (IOException e1) {
                }
            }
            if (writere != null) {
                try {
                    writere.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public static void sortData() {
        int length = 98287, num, len = (int) (0.2 * length);
        Random random = new Random();
        Set<Integer> testRows = new HashSet<>();
        while (testRows.size() < len) {
            num = random.nextInt(length) + 1;
            testRows.add(num);
        }
        System.out.printf("test:%d, len: %d, length: %d\n", testRows.size(), len, length);

        File inputFile = new File("dataset\\meetup\\event.csv");
        File trainFile = new File("dataset\\meetup\\train.csv");
        File testFile = new File("dataset\\meetup\\test.csv");
        BufferedReader reader = null;
        BufferedWriter train = null;
        BufferedWriter test = null;
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            train = new BufferedWriter(new FileWriter(trainFile));
            test = new BufferedWriter(new FileWriter(testFile));

            String line;
            int lineNum = 1;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (testRows.contains(lineNum)) {
                    test.write(line + "\n");
                } else {
                    train.write(line + "\n");
                }
                lineNum++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (train != null) {
                try {
                    train.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (test != null) {
                try {
                    test.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void cleanLocations() {
        File inputFile = new File("dataset\\meetup\\events.csv");
        File groupFile = new File("dataset\\meetup\\group.csv");
        File groupsFile = new File("dataset\\meetup\\groups.csv");
        BufferedReader reader = null;
        BufferedReader group = null;
        BufferedWriter groups = null;
        Set<String> remain = new HashSet<>();
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            group = new BufferedReader(new FileReader(groupFile));
            groups = new BufferedWriter(new FileWriter(groupsFile));

            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] strs = line.split(",");
                remain.add(strs[3]);
            }

            groups.write(group.readLine() + "\n");
            while ((line = group.readLine()) != null) {
                String[] strs = line.split(",");
                if (remain.contains(strs[0])) {
                    groups.write(line + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (group != null) {
                try {
                    group.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (groups != null) {
                try {
                    groups.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void replaceData() {
        File lmapFile = new File("dataset\\meetup\\l_map.csv");
        File locationsFile = new File("dataset\\meetup\\locations.csv");
        File locationFile = new File("dataset\\meetup\\location.csv");
        BufferedReader lmap = null;
        BufferedReader locations = null;
        BufferedWriter location = null;
        Map<String, String> locationReplace = new HashMap<>();
        try {
            lmap = new BufferedReader(new FileReader(lmapFile));
            locations = new BufferedReader(new FileReader(locationsFile));
            location = new BufferedWriter(new FileWriter(locationFile));

            String line;
            lmap.readLine();
            while ((line = lmap.readLine()) != null) {
                String[] tmp = line.split(",");
                locationReplace.put(tmp[1], tmp[0]);
            }
            location.write("l_id,longitude,latitude\n");
            locations.readLine();
            while ((line = locations.readLine()) != null) {
                String[] tmp = line.split(",");
                String r = locationReplace.get(tmp[0]) + "," + tmp[1] + "," + tmp[2] + "\n";
                location.write(r);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (lmap != null) {
                try {
                    lmap.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (locations != null) {
                try {
                    locations.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (location != null) {
                try {
                    location.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void simplifyEvent() {
        File lmapFile = new File("dataset\\meetup\\l_map.csv");
        File locationsFile = new File("dataset\\meetup\\locations.csv");
        File locationFile = new File("dataset\\meetup\\location.csv");
        BufferedReader lmap = null;
        BufferedReader locations = null;
        BufferedWriter location = null;
        Map<String, String> locationReplace = new HashMap<>();
        try {
            lmap = new BufferedReader(new FileReader(lmapFile));
            locations = new BufferedReader(new FileReader(locationsFile));
            location = new BufferedWriter(new FileWriter(locationFile));

            String line;
            lmap.readLine();
            while ((line = lmap.readLine()) != null) {
                String[] tmp = line.split(",");
                locationReplace.put(tmp[1], tmp[0]);
            }
            location.write("l_id,longitude,latitude\n");
            locations.readLine();
            while ((line = locations.readLine()) != null) {
                String[] tmp = line.split(",");
                String r = locationReplace.get(tmp[0]) + "," + tmp[1] + "," + tmp[2] + "\n";
                location.write(r);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (lmap != null) {
                try {
                    lmap.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (locations != null) {
                try {
                    locations.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (location != null) {
                try {
                    location.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static void setLocTimes(ArrayList<Long>[] locTimes) {
        File inputFile = new File("dataset\\meetup\\events.csv");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(inputFile));

            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.split(",");
                int l_id = Integer.parseInt(tmp[1]);
                long time = Long.parseLong(tmp[2]);
                if (locTimes[l_id] == null) {
                    locTimes[l_id] = new ArrayList<>();
                }
                locTimes[l_id].add(time);
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    private static void setFinalDataset() {
        int group_num = 1134, events_num = 98287;
        Long[] eventTimes = new Long[events_num];
        ArrayList<Integer>[] groupEvents = new ArrayList[group_num];

        File inputFile = new File("dataset\\meetup\\group_events.csv");
        File eventFile = new File("dataset\\meetup\\events.csv");
        File trainFile = new File("dataset\\meetup\\train.csv");
        File testFile = new File("dataset\\meetup\\test.csv");
        BufferedReader reader = null;
        BufferedReader ereader = null;
        BufferedWriter train = null;
        BufferedWriter test = null;
        String line;
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            ereader = new BufferedReader(new FileReader(eventFile));
            train = new BufferedWriter(new FileWriter(trainFile));
            test = new BufferedWriter(new FileWriter(testFile));

            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.split(",");
                int g_id = Integer.parseInt(tmp[0]);
                int e_id = Integer.parseInt(tmp[1]);
                if (g_id >= group_num) {
                    System.out.println("group number is wrong.");
                    return;
                }
                if (groupEvents[g_id] == null) {
                    groupEvents[g_id] = new ArrayList<>();
                }
                groupEvents[g_id].add(e_id);
            }

            ereader.readLine();
            while ((line = ereader.readLine()) != null) {
                String[] tmp = line.split(",");
                int e_id = Integer.parseInt(tmp[0]);
                long time = Long.parseLong(tmp[2]);
                if (e_id >= events_num) {
                    System.out.println("events number is wrong.");
                    return;
                }
                eventTimes[e_id] = time;
            }

            double rate = 0.8;
            train.write("g_id,e_id\n");
            test.write("g_id,e_id\n");
            for (int k = 0; k < groupEvents.length; k++) {
                List<Integer> list = groupEvents[k];
                list.sort(Comparator.comparingLong((Integer o) -> eventTimes[o]));
                int j = (int) (rate * list.size());
                for (int i = 0; i < list.size(); i++) {
                    line = k + "," + list.get(i) + "\n";
                    if (i < j) {
                        train.write(line);
                    } else {
                        test.write(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (ereader != null) {
                try {
                    ereader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (train != null) {
                try {
                    train.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (test != null) {
                try {
                    test.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    static void saveModelData(double[][] arrs, String fileName) {
        File file = new File("dataset\\meetup\\model\\" + fileName);
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
        File file = new File("dataset\\meetup\\model\\" + fileName);
        List<double[]> res = new ArrayList<>();
        BufferedReader reader = null;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.split("\t");
                double[] items = new double[tmp.length];
                for (int i = 0; i < tmp.length; i++) {
                    items[i] = Double.parseDouble(tmp[i]);
                }
                res.add(items);
            }
            return res.toArray(new double[][] {});
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

    static void saveResults(int[][] arrs) {
        File file = new File("dataset\\meetup\\reclist.txt");
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

    static void saveEvaluation(int topn, int hit, int truth, int recall, int precision) {
        File file = new File("dataset\\meetup\\evaluation.txt");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("topn=" + topn + "\n");
            writer.write("hit=" + hit + "\n");
            writer.write("pre=" + precision + "\n");
            writer.write("rec=" + recall + "\n");
            writer.write("truth=" + truth + "\n");
            String p = String.format("Precision=%.4f%%\n", 100.0 * hit / precision);
            String r = String.format("Recall=%.4f%%\n", 100.0 * hit / recall);
            writer.write(p);
            writer.write(r);
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
    private static String[] months = new String[] { "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月",
            "十二月" };
    private static String[] weeks = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };

    private static int getMonth(long time) {
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.MONTH);
    }

    private static int getWeek(long time) {
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static void main(String[] args) {
        setFinalDataset();

        /*
         * int n = 10005; ArrayList<Long>[] locTimes = new ArrayList[n];
         * setLocTimes(locTimes); int week, max_week, week_num50 = 0, week_num80 = 0;
         * double weekRate; for (int i = 0; i < n; i++) { int[] tmp_week = new int[7];
         * max_week = 0; for (Long time : locTimes[i]) { week = getWeek(time);
         * tmp_week[week]++; max_week = Math.max(max_week, tmp_week[week]);
         * 
         * } weekRate = 1.0 * max_week / locTimes[i].size(); if (weekRate >= 0.8)
         * week_num80++; else if (weekRate >= 0.5) week_num50++; }
         * System.out.println(1.0 * week_num80 / n); System.out.println(1.0 * week_num50
         * / n);
         */

        /*
         * System.out.println("-----------Month-----------"); for (int i = 0; i < 100;
         * i++) { for (int j = 0; j < locTimes[i].size() - 1; j++) {
         * System.out.print(getMonth(locTimes[i].get(j)) + " "); }
         * System.out.println(getMonth(locTimes[i].get(locTimes[i].size() - 1))); }
         * System.out.println("-----------Month-----------");
         */
        /*
         * System.out.println("-----------Week-----------"); for (int i = 0; i < 100;
         * i++) { for (int j = 0; j < locTimes[i].size() - 1; j++) {
         * System.out.print(getWeek(locTimes[i].get(j)) + " "); }
         * System.out.println(getWeek(locTimes[i].get(locTimes[i].size() - 1))); }
         * System.out.println("-----------Week-----------");
         */

        // System.out.println(locations[6]);
        // for(int i=0;i<10;i++){
        // for(int j=0;j<locations[43].length;j++)
        // System.out.print(locations[43][j]+" ");
        // System.out.println();
        // }

        // for(int i : train.get(2))
        // System.out.println(i);
    }
}
